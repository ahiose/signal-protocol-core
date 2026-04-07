package com.opensignal.protocol.gb20999.netty;

import com.opensignal.protocol.common.exception.CodecException;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.core.frame.FrameDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * UDP {@link MessageToMessageDecoder} for GB/T 20999 frames without escape characters.
 */
@ChannelHandler.Sharable
public class Gb20999UdpFrameDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private static final Logger LOG = LoggerFactory.getLogger(Gb20999UdpFrameDecoder.class);

    /** Minimum bytes inside datagram: START + {@link FrameDecoder} payload + END. */
    private static final int MIN_DATAGRAM = 1 + 14 + 1;

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
        ByteBuf content = msg.content();
        int n = content.readableBytes();
        if (n < MIN_DATAGRAM) {
            LOG.warn("UDP datagram too short: {} bytes (from {})", n, msg.sender());
            return;
        }
        int r = content.readerIndex();
        if (content.getByte(r) != Frame.START_BYTE || content.getByte(r + n - 1) != Frame.END_BYTE) {
            LOG.warn("UDP datagram missing 0x7E/0x7D delimiters (from {})", msg.sender());
            return;
        }
        byte[] raw = new byte[n - 2];
        content.getBytes(r + 1, raw);
        try {
            out.add(FrameDecoder.decode(raw));
        } catch (CodecException e) {
            LOG.warn("UDP frame decode failed from {}: {}", msg.sender(), e.getMessage(), e);
        }
    }
}
