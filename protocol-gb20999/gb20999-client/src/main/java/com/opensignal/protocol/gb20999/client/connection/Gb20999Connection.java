package com.opensignal.protocol.gb20999.client.connection;

import com.opensignal.protocol.common.exception.CodecException;
import com.opensignal.protocol.common.spi.ConnectionStrategy;
import com.opensignal.protocol.common.spi.VendorProfile;
import com.opensignal.protocol.common.transport.Connection;
import com.opensignal.protocol.gb20999.client.Gb20999Client;
import com.opensignal.protocol.gb20999.core.frame.EscapeCodec;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.core.frame.FrameDecoder;
import com.opensignal.protocol.gb20999.core.frame.FrameEncoder;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * {@link Connection} adapter around {@link Gb20999Client}.
 */
public class Gb20999Connection implements Connection {

    private final String id;
    private final Gb20999Client client;

    public Gb20999Connection(String id, Gb20999Client client) {
        this.id = id;
        this.client = client;
    }

    /**
     * Typed access to the underlying client.
     */
    public Gb20999Client client() {
        return client;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return client.getConfig().getRemoteAddress();
    }

    @Override
    public VendorProfile vendorProfile() {
        return client.getVendorProfile();
    }

    @Override
    public boolean isActive() {
        return client.isConnected();
    }

    @Override
    public CompletableFuture<byte[]> send(byte[] data) {
        final CompletableFuture<byte[]> out = new CompletableFuture<>();
        try {
            Frame request = decodeWireBytes(data);
            client.sendFrame(request).whenComplete(new java.util.function.BiConsumer<Frame, Throwable>() {
                @Override
                public void accept(Frame response, Throwable error) {
                    if (error != null) {
                        out.completeExceptionally(error);
                    } else {
                        try {
                            out.complete(encodeWireBytes(response));
                        } catch (Exception e) {
                            out.completeExceptionally(e);
                        }
                    }
                }
            });
        } catch (RuntimeException e) {
            out.completeExceptionally(e);
        }
        return out;
    }

    @Override
    public CompletableFuture<byte[]> send(byte[] data, long timeoutMs) {
        final CompletableFuture<byte[]> pending = send(data);
        if (client.getChannel() == null || client.getChannel().eventLoop().isShutdown()) {
            return pending;
        }
        final java.util.concurrent.ScheduledFuture<?> timeoutTask = client.getChannel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                if (!pending.isDone()) {
                    pending.completeExceptionally(
                            new com.opensignal.protocol.common.exception.TimeoutException("Send timed out after " + timeoutMs + " ms"));
                }
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);

        pending.whenComplete(new java.util.function.BiConsumer<byte[], Throwable>() {
            @Override
            public void accept(byte[] bytes, Throwable throwable) {
                timeoutTask.cancel(false);
            }
        });
        return pending;
    }

    @Override
    public void close() {
        client.close();
    }

    private boolean applyEscape() {
        return client.getConfig().getTransport() == ConnectionStrategy.TCP;
    }

    private byte[] encodeWireBytes(Frame frame) {
        return FrameEncoder.encode(frame, applyEscape());
    }

    /**
     * Accepts a full delimited wire frame (0x7E … 0x7D) or raw inner bytes (length + body + CRC) as produced by
     * {@link FrameDecoder}.
     */
    static Frame decodeWireBytes(byte[] data) {
        if (data == null || data.length == 0) {
            throw new CodecException("Empty frame bytes");
        }
        if (data[0] == Frame.START_BYTE && data[data.length - 1] == Frame.END_BYTE) {
            byte[] escapedInner = Arrays.copyOfRange(data, 1, data.length - 1);
            byte[] raw = EscapeCodec.unescape(escapedInner);
            return FrameDecoder.decode(raw);
        }
        return FrameDecoder.decode(data);
    }
}
