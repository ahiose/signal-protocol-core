package com.opensignal.protocol.gb20999.netty;

import com.opensignal.protocol.common.exception.CodecException;
import com.opensignal.protocol.gb20999.core.frame.EscapeCodec;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.core.frame.FrameDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * TCP {@link ByteToMessageDecoder} for GB/T 20999 frames with start/end markers and escape rules.
 *
 * <p>{@link ByteToMessageDecoder} is stateful (cumulation buffer) and MUST NOT be shared across channels.
 */
public class Gb20999FrameDecoder extends ByteToMessageDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(Gb20999FrameDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (true) {
            int readerIdx = in.readerIndex();
            int writerIdx = in.writerIndex();
            if (readerIdx >= writerIdx) {
                return;
            }

            int start = indexOfStartByte(in, readerIdx, writerIdx);
            if (start < 0) {
                in.skipBytes(in.readableBytes());
                return;
            }
            if (start > readerIdx) {
                in.skipBytes(start - readerIdx);
            }

            int frameStart = in.readerIndex();
            int endIdx = findUnescapedEndByte(in, frameStart + 1, writerIdx);
            if (endIdx < 0) {
                return;
            }

            int totalLen = endIdx - frameStart + 1;
            int innerLen = totalLen - 2;
            byte[] escapedBody = new byte[innerLen];
            in.getBytes(frameStart + 1, escapedBody);
            in.skipBytes(totalLen);

            try {
                byte[] raw = EscapeCodec.unescape(escapedBody);
                Frame frame = FrameDecoder.decode(raw);
                out.add(frame);
            } catch (CodecException e) {
                LOG.warn("TCP frame decode failed: {}", e.getMessage(), e);
            }
        }
    }

    private static int indexOfStartByte(ByteBuf in, int from, int to) {
        for (int i = from; i < to; i++) {
            if (in.getByte(i) == Frame.START_BYTE) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the first frame {@link Frame#END_BYTE} that is not consumed as escaped payload
     * (same semantics as {@link EscapeCodec#unescape(byte[])}).
     */
    private static int findUnescapedEndByte(ByteBuf in, int from, int to) {
        boolean escapeNext = false;
        for (int i = from; i < to; i++) {
            byte b = in.getByte(i);
            if (escapeNext) {
                escapeNext = false;
                continue;
            }
            if (b == Frame.ESCAPE_BYTE) {
                escapeNext = true;
                continue;
            }
            if (b == Frame.END_BYTE) {
                return i;
            }
        }
        return -1;
    }
}
