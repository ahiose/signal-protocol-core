package com.opensignal.protocol.gb20999.netty;

import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.core.frame.FrameEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * UDP {@link MessageToMessageEncoder} for {@link Frame} without escape encoding.
 */
public class Gb20999UdpFrameEncoder extends MessageToMessageEncoder<Frame> {

    private final InetSocketAddress remoteAddress;

    public Gb20999UdpFrameEncoder(InetSocketAddress remoteAddress) {
        if (remoteAddress == null) {
            throw new IllegalArgumentException("remoteAddress");
        }
        this.remoteAddress = remoteAddress;
    }

    public InetSocketAddress remoteAddress() {
        return remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Frame msg, List<Object> out) {
        byte[] wire = FrameEncoder.encode(msg, false);
        ByteBuf buf = ctx.alloc().buffer(wire.length);
        buf.writeBytes(wire);
        out.add(new DatagramPacket(buf, remoteAddress));
    }
}
