package com.opensignal.protocol.gb20999.netty;

import io.netty.channel.ChannelPipeline;

import java.net.InetSocketAddress;

/**
 * Factory helpers to install GB/T 20999 Netty codecs on a {@link ChannelPipeline}.
 */
public final class Gb20999ChannelInitializer {

    private static final Gb20999FrameEncoder TCP_ENCODER = new Gb20999FrameEncoder();
    private static final Gb20999UdpFrameDecoder UDP_DECODER = new Gb20999UdpFrameDecoder();

    private Gb20999ChannelInitializer() {
    }

    /**
     * Adds TCP frame decoder and encoder (escaped wire format).
     * Decoder is per-channel (stateful cumulation buffer).
     */
    public static void initTcpPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("gb20999FrameDecoder", new Gb20999FrameDecoder());
        pipeline.addLast("gb20999FrameEncoder", TCP_ENCODER);
    }

    /**
     * Adds UDP frame decoder and encoder (no escape). The encoder sends every {@link com.opensignal.protocol.gb20999.core.frame.Frame}
     * to {@code remoteAddress}.
     */
    public static void initUdpPipeline(ChannelPipeline pipeline, InetSocketAddress remoteAddress) {
        pipeline.addLast("gb20999UdpFrameDecoder", UDP_DECODER);
        pipeline.addLast("gb20999UdpFrameEncoder", new Gb20999UdpFrameEncoder(remoteAddress));
    }
}
