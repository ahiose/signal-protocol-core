package com.opensignal.protocol.gb20999.netty;

import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.core.frame.FrameEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * TCP {@link MessageToByteEncoder} for {@link Frame} with escape encoding.
 */
@ChannelHandler.Sharable
public class Gb20999FrameEncoder extends MessageToByteEncoder<Frame> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Frame msg, ByteBuf out) {
        byte[] wire = FrameEncoder.encode(msg, true);
        out.writeBytes(wire);
    }
}
