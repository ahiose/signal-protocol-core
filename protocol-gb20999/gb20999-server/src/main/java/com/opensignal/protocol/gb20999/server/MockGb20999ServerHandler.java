package com.opensignal.protocol.gb20999.server;

import com.opensignal.protocol.gb20999.core.frame.DataValue;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.core.frame.FrameType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Minimal mock controller: answers query, set, and heartbeat so local SDK tests work.
 */
final class MockGb20999ServerHandler extends SimpleChannelInboundHandler<Frame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame req) {
        FrameType type = req.getFrameType();
        if (type == FrameType.HEARTBEAT_QUERY) {
            ctx.writeAndFlush(replyFrame(req, FrameType.HEARTBEAT_REPLY, Collections.<DataValue>emptyList()));
            return;
        }
        if (type == FrameType.QUERY) {
            List<DataValue> replyValues = new ArrayList<>();
            for (DataValue q : req.getDataValues()) {
                if (q.getDataClassId() == 1 && q.getObjectId() == 1) {
                    byte[] name = "MockManufacturer".getBytes(StandardCharsets.UTF_8);
                    replyValues.add(DataValue.of(
                            q.getIndex(), q.getDataClassId(), q.getObjectId(),
                            q.getAttributeId(), q.getElementId(), name));
                } else {
                    replyValues.add(DataValue.queryOf(
                            q.getIndex(), q.getDataClassId(), q.getObjectId(),
                            q.getAttributeId(), q.getElementId()));
                }
            }
            ctx.writeAndFlush(replyFrame(req, FrameType.QUERY_REPLY, replyValues));
            return;
        }
        if (type == FrameType.SET) {
            ctx.writeAndFlush(replyFrame(req, FrameType.SET_REPLY, Collections.<DataValue>emptyList()));
        }
    }

    private static Frame replyFrame(Frame req, FrameType replyType, List<DataValue> dataValues) {
        return Frame.builder()
                .version(req.getVersion())
                .upperId(req.getUpperId())
                .signalId(req.getSignalId())
                .crossId(req.getCrossId())
                .sequence(req.getSequence())
                .frameType(replyType)
                .dataValues(dataValues)
                .build();
    }
}
