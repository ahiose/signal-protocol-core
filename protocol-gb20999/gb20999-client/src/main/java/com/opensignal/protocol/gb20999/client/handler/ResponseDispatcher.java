package com.opensignal.protocol.gb20999.client.handler;

import com.opensignal.protocol.gb20999.client.Gb20999Client;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dispatches decoded {@link Frame} instances to {@link Gb20999Client#handleResponse(Frame)}.
 */
public class ResponseDispatcher extends SimpleChannelInboundHandler<Frame> {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseDispatcher.class);

    private final Gb20999Client client;

    public ResponseDispatcher(Gb20999Client client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame frame) {
        client.handleResponse(frame);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOG.info("GB20999 channel inactive: {}", ctx.channel().remoteAddress());
        client.onChannelInactive(ctx);
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.warn("GB20999 channel exception, closing: {}", cause.toString());
        ctx.close();
    }
}
