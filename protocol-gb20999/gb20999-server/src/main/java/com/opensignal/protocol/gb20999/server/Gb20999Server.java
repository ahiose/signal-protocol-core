package com.opensignal.protocol.gb20999.server;

import com.opensignal.protocol.gb20999.netty.Gb20999ChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Embedded TCP server that speaks GB/T 20999 for tests and samples.
 */
public class Gb20999Server implements AutoCloseable {

    private final Gb20999ServerConfig config;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private volatile boolean started;

    public static Gb20999Server create(Gb20999ServerConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config is required");
        }
        return new Gb20999Server(config);
    }

    private Gb20999Server(Gb20999ServerConfig config) {
        this.config = config;
    }

    public void start() {
        synchronized (this) {
            if (started) {
                return;
            }
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            Gb20999ChannelInitializer.initTcpPipeline(ch.pipeline());
                            ch.pipeline().addLast(new MockGb20999ServerHandler());
                        }
                    });
            ChannelFuture bindFuture = bootstrap.bind(config.getBindHost(), config.getBindPort())
                    .syncUninterruptibly();
            serverChannel = bindFuture.channel();
            started = true;
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            if (!started) {
                return;
            }
            try {
                if (serverChannel != null) {
                    serverChannel.close().syncUninterruptibly();
                    serverChannel = null;
                }
            } finally {
                if (bossGroup != null) {
                    bossGroup.shutdownGracefully();
                    bossGroup = null;
                }
                if (workerGroup != null) {
                    workerGroup.shutdownGracefully();
                    workerGroup = null;
                }
                started = false;
            }
        }
    }
}
