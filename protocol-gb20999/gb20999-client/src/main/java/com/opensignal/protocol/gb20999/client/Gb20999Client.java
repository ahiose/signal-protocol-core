package com.opensignal.protocol.gb20999.client;

import com.opensignal.protocol.common.exception.ConnectionException;
import com.opensignal.protocol.common.exception.ProtocolException;
import com.opensignal.protocol.common.exception.TimeoutException;
import com.opensignal.protocol.common.spi.ConnectionStrategy;
import com.opensignal.protocol.common.spi.VendorProfile;
import com.opensignal.protocol.common.spi.VendorRegistry;
import com.opensignal.protocol.gb20999.client.handler.HeartbeatHandler;
import com.opensignal.protocol.gb20999.client.handler.ResponseDispatcher;
import com.opensignal.protocol.gb20999.core.frame.DataValue;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.core.frame.FrameType;
import com.opensignal.protocol.gb20999.netty.Gb20999ChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * High-level Netty client for GB/T 20999 controllers.
 */
public class Gb20999Client implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Gb20999Client.class);

    private static final int COMMAND_DATA_CLASS_ID = com.opensignal.protocol.gb20999.core.constant.DataClassId.COMMAND;

    private final Gb20999ClientConfig config;
    private final VendorProfile vendorProfile;

    private final Object connectLock = new Object();
    private EventLoopGroup workerGroup;
    private volatile Channel channel;
    private final AtomicInteger sequenceCounter = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, CompletableFuture<Frame>> pendingRequests = new ConcurrentHashMap<>();
    private volatile ScheduledFuture<?> heartbeatTask;
    private volatile boolean connected;
    private volatile boolean closed;

    private Gb20999Client(Gb20999ClientConfig config, VendorProfile vendorProfile) {
        this.config = config;
        this.vendorProfile = vendorProfile;
    }

    public static Gb20999Client create(Gb20999ClientConfig config) {
        if (config == null || config.getRemoteAddress() == null) {
            throw new IllegalArgumentException("config and remoteAddress are required");
        }
        VendorProfile profile = VendorRegistry.getInstance().resolve(config.getVendorId());
        return new Gb20999Client(config, profile);
    }

    public Gb20999ClientConfig getConfig() {
        return config;
    }

    public VendorProfile getVendorProfile() {
        return vendorProfile;
    }

    public boolean isConnected() {
        return connected && channel != null && channel.isActive();
    }

    public Channel getChannel() {
        return channel;
    }

    public void connect() {
        synchronized (connectLock) {
            if (closed) {
                throw new ConnectionException("Client is closed");
            }
            if (channel != null && channel.isActive()) {
                return;
            }
            if (workerGroup == null) {
                workerGroup = new NioEventLoopGroup(1);
            }
            bootstrapConnect();
            connected = true;
            startHeartbeat();
        }
    }

    private void bootstrapConnect() {
        ConnectionStrategy strategy = config.getTransport();
        if (strategy == ConnectionStrategy.SERIAL) {
            throw new ConnectionException("SERIAL transport is not supported by Gb20999Client");
        }

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);

        if (strategy == ConnectionStrategy.TCP) {
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) Math.min(config.getConnectTimeoutMs(), Integer.MAX_VALUE));
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    Gb20999ChannelInitializer.initTcpPipeline(ch.pipeline());
                    ch.pipeline().addLast(new ResponseDispatcher(Gb20999Client.this));
                }
            });
            ChannelFuture future = bootstrap.connect(config.getRemoteAddress());
            if (!future.awaitUninterruptibly(config.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)) {
                future.cancel(false);
                throw new ConnectionException("TCP connect timeout: " + config.getRemoteAddress());
            }
            if (!future.isSuccess()) {
                throw new ConnectionException("TCP connect failed: " + config.getRemoteAddress(), future.cause());
            }
            channel = future.channel();
        } else {
            bootstrap.channel(NioDatagramChannel.class);
            bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
                @Override
                protected void initChannel(DatagramChannel ch) {
                    Gb20999ChannelInitializer.initUdpPipeline(ch.pipeline(), config.getRemoteAddress());
                    ch.pipeline().addLast(new ResponseDispatcher(Gb20999Client.this));
                }
            });
            ChannelFuture future = bootstrap.connect(config.getRemoteAddress());
            if (!future.awaitUninterruptibly(config.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)) {
                future.cancel(false);
                throw new ConnectionException("UDP connect timeout: " + config.getRemoteAddress());
            }
            if (!future.isSuccess()) {
                throw new ConnectionException("UDP connect failed: " + config.getRemoteAddress(), future.cause());
            }
            channel = future.channel();
        }
    }

    public CompletableFuture<Frame> sendFrame(Frame frame) {
        if (channel == null || !channel.isActive()) {
            CompletableFuture<Frame> failed = new CompletableFuture<>();
            failed.completeExceptionally(new ConnectionException("Not connected"));
            return failed;
        }
        final int seq = frame.getSequence();
        final CompletableFuture<Frame> future = new CompletableFuture<>();
        pendingRequests.put(seq, future);
        channel.writeAndFlush(frame).addListener(f -> {
            if (!f.isSuccess()) {
                pendingRequests.remove(seq, future);
                future.completeExceptionally(
                        f.cause() != null ? f.cause() : new ConnectionException("Write failed"));
            }
        });
        return future;
    }

    public Frame query(int dataClassId, int objectId, int attributeId, int elementId) {
        List<DataValue> dataValues = new ArrayList<>();
        dataValues.add(DataValue.queryOf(1, dataClassId, objectId, attributeId, elementId));
        Frame frame = buildFrame(FrameType.QUERY, dataValues);
        return awaitFrame(frame, config.getResponseTimeoutMs());
    }

    public Frame query(int dataClassId, int objectId) {
        return query(dataClassId, objectId, 0, 0);
    }

    public Frame set(List<DataValue> dataValues) {
        if (dataValues == null) {
            throw new IllegalArgumentException("dataValues");
        }
        Frame frame = buildFrame(FrameType.SET, dataValues);
        return awaitFrame(frame, config.getResponseTimeoutMs());
    }

    public void command(int commandCode) {
        List<DataValue> dataValues = new ArrayList<>();
        dataValues.add(DataValue.of(1, COMMAND_DATA_CLASS_ID, 1, 0, 0,
                new byte[]{(byte) (commandCode & 0xFF)}));
        set(dataValues);
    }

    private Frame awaitFrame(Frame frame, long timeoutMs) {
        CompletableFuture<Frame> future = sendFrame(frame);
        int seq = frame.getSequence();
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            pendingRequests.remove(seq, future);
            throw new TimeoutException("GB20999 operation timed out after " + timeoutMs + " ms");
        } catch (InterruptedException e) {
            pendingRequests.remove(seq, future);
            Thread.currentThread().interrupt();
            throw new ProtocolException("Interrupted", e);
        } catch (ExecutionException e) {
            Throwable c = e.getCause();
            if (c instanceof RuntimeException) {
                throw (RuntimeException) c;
            }
            throw new ProtocolException("Operation failed", c);
        }
    }

    @Override
    public void close() {
        synchronized (connectLock) {
            closed = true;
            cancelHeartbeat();
            connected = false;
            failAllPending(new ConnectionException("Client closed"));
            if (channel != null) {
                channel.close().awaitUninterruptibly(5, TimeUnit.SECONDS);
                channel = null;
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully(0, 5, TimeUnit.SECONDS).awaitUninterruptibly(10, TimeUnit.SECONDS);
                workerGroup = null;
            }
        }
    }

    /**
     * Next sequence number in {@code 0–255}, wrapping.
     */
    int nextSequence() {
        return sequenceCounter.updateAndGet(v -> {
            int n = v + 1;
            if (n > 255) {
                n = 0;
            }
            return n;
        }) & 0xFF;
    }

    Frame buildFrame(FrameType type, List<DataValue> dataValues) {
        int seq = nextSequence();
        if (type == FrameType.QUERY) {
            return Frame.query(config.getUpperId(), config.getSignalId(), config.getCrossId(), seq, dataValues);
        }
        if (type == FrameType.SET) {
            return Frame.set(config.getUpperId(), config.getSignalId(), config.getCrossId(), seq, dataValues);
        }
        throw new ProtocolException("Unsupported frame type for buildFrame: " + type);
    }

    void startHeartbeat() {
        cancelHeartbeat();
        if (config.getHeartbeatIntervalMs() <= 0 || channel == null) {
            return;
        }
        heartbeatTask = channel.eventLoop().scheduleAtFixedRate(
                new HeartbeatHandler(this),
                config.getHeartbeatIntervalMs(),
                config.getHeartbeatIntervalMs(),
                TimeUnit.MILLISECONDS);
    }

    private void cancelHeartbeat() {
        ScheduledFuture<?> task = heartbeatTask;
        if (task != null) {
            task.cancel(false);
            heartbeatTask = null;
        }
    }

    public void handleResponse(Frame frame) {
        FrameType type = frame.getFrameType();
        if (!type.isResponse() && !type.isError() && type != FrameType.HEARTBEAT_REPLY) {
            return;
        }
        CompletableFuture<Frame> pending = pendingRequests.remove(frame.getSequence());
        if (pending == null) {
            return;
        }
        if (type.isError()) {
            pending.completeExceptionally(new ProtocolException("Device error reply: " + type + " seq=" + frame.getSequence()));
        } else {
            pending.complete(frame);
        }
    }

    public void runHeartbeatTick() {
        if (!isConnected()) {
            return;
        }
        int seq = nextSequence();
        Frame hb = Frame.heartbeat(config.getUpperId(), config.getSignalId(), config.getCrossId(), seq);
        CompletableFuture<Frame> future = sendFrame(hb);
        try {
            future.get(config.getResponseTimeoutMs(), TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            LOG.warn("Heartbeat: no response within {} ms", config.getResponseTimeoutMs());
            pendingRequests.remove(seq, future);
        } catch (Exception e) {
            LOG.warn("Heartbeat failed: {}", e.toString());
            pendingRequests.remove(seq, future);
        }
    }

    public void onChannelInactive(ChannelHandlerContext ctx) {
        connected = false;
        cancelHeartbeat();
        failAllPending(new ConnectionException("Channel disconnected"));

        if (closed || !config.isAutoReconnect() || workerGroup == null || workerGroup.isShutdown()) {
            return;
        }

        scheduleReconnect(1);
    }

    private void scheduleReconnect(final int attemptNumber) {
        if (attemptNumber > config.getMaxReconnectAttempts()) {
            LOG.warn("Reconnect stopped after {} failed attempt(s)", config.getMaxReconnectAttempts());
            return;
        }
        workerGroup.schedule(new Runnable() {
            @Override
            public void run() {
                if (closed) {
                    return;
                }
                synchronized (connectLock) {
                    if (closed || (channel != null && channel.isActive())) {
                        return;
                    }
                    try {
                        bootstrapConnect();
                        connected = true;
                        startHeartbeat();
                        LOG.info("GB20999 reconnect succeeded (attempt {})", attemptNumber);
                    } catch (Exception e) {
                        LOG.warn("GB20999 reconnect attempt {} failed: {}", attemptNumber, e.toString());
                        scheduleReconnect(attemptNumber + 1);
                    }
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    private void failAllPending(Throwable cause) {
        for (Map.Entry<Integer, CompletableFuture<Frame>> e : pendingRequests.entrySet()) {
            e.getValue().completeExceptionally(cause);
        }
        pendingRequests.clear();
    }
}
