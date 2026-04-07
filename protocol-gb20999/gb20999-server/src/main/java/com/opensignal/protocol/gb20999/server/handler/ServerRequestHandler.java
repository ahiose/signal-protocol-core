package com.opensignal.protocol.gb20999.server.handler;

import com.opensignal.protocol.gb20999.core.constant.DataClassId;
import com.opensignal.protocol.gb20999.core.frame.DataValue;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.core.frame.FrameType;
import com.opensignal.protocol.gb20999.server.Gb20999ServerConfig;
import com.opensignal.protocol.gb20999.server.MockSignalData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles decoded {@link Frame}s for the mock server.
 */
public class ServerRequestHandler extends SimpleChannelInboundHandler<Frame> {

    private static final Logger log = LoggerFactory.getLogger(ServerRequestHandler.class);

    private final Gb20999ServerConfig config;
    private final MockSignalData mockData;
    private final ServerTrapSender trapSender;

    public ServerRequestHandler(Gb20999ServerConfig config, MockSignalData mockData, ServerTrapSender trapSender) {
        this.config = config;
        this.mockData = mockData;
        this.trapSender = trapSender;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        trapSender.register(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        trapSender.unregister(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame msg) throws Exception {
        FrameType type = msg.getFrameType();
        if (type == FrameType.HEARTBEAT_QUERY) {
            ctx.writeAndFlush(heartbeatReply(msg));
            return;
        }
        if (type == FrameType.QUERY) {
            ctx.writeAndFlush(queryReply(msg));
            return;
        }
        if (type == FrameType.SET) {
            applySet(msg);
            ctx.writeAndFlush(setReply(msg));
            return;
        }
        log.debug("Ignoring frame type {}", type);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Channel exception, closing", cause);
        ctx.close();
    }

    private Frame heartbeatReply(Frame request) {
        return Frame.builder()
                .version(request.getVersion())
                .upperId(config.getUpperId())
                .signalId(request.getSignalId())
                .crossId(request.getCrossId())
                .sequence(request.getSequence())
                .frameType(FrameType.HEARTBEAT_REPLY)
                .dataValues(Collections.<DataValue>emptyList())
                .build();
    }

    private Frame queryReply(Frame request) {
        List<DataValue> out = new ArrayList<DataValue>();
        int n = request.dataValueCount();
        for (int i = 0; i < n; i++) {
            DataValue q = request.getDataValue(i);
            byte[] payload = resolveQueryPayload(q);
            out.add(DataValue.of(
                    q.getIndex(),
                    q.getDataClassId(),
                    q.getObjectId(),
                    q.getAttributeId(),
                    q.getElementId(),
                    payload));
        }
        return Frame.builder()
                .version(request.getVersion())
                .upperId(config.getUpperId())
                .signalId(request.getSignalId())
                .crossId(request.getCrossId())
                .sequence(request.getSequence())
                .frameType(FrameType.QUERY_REPLY)
                .dataValues(Collections.unmodifiableList(out))
                .build();
    }

    private byte[] resolveQueryPayload(DataValue q) {
        int dc = q.getDataClassId();
        int oid = q.getObjectId();
        if (dc == DataClassId.DEVICE) {
            return mockData.getDeviceInfoData(oid);
        }
        if (dc == DataClassId.PHASE) {
            return mockData.getBasicInfoData(oid);
        }
        if (dc == Gb20999ServerConfig.RUN_STATUS_DATA_CLASS_ID) {
            return mockData.getRunStatusData(oid, q.getAttributeId());
        }
        log.debug("No mock for query dataClassId={} objectId={}", dc, oid);
        return new byte[0];
    }

    private void applySet(Frame request) {
        int n = request.dataValueCount();
        for (int i = 0; i < n; i++) {
            DataValue dv = request.getDataValue(i);
            if (!dv.hasData()) {
                continue;
            }
            mockData.handleSet(dv.getDataClassId(), dv.getObjectId(), dv.getData());
        }
    }

    private Frame setReply(Frame request) {
        return Frame.builder()
                .version(request.getVersion())
                .upperId(config.getUpperId())
                .signalId(request.getSignalId())
                .crossId(request.getCrossId())
                .sequence(request.getSequence())
                .frameType(FrameType.SET_REPLY)
                .dataValues(Collections.<DataValue>emptyList())
                .build();
    }
}
