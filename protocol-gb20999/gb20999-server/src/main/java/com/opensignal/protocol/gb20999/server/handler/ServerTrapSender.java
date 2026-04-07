package com.opensignal.protocol.gb20999.server.handler;

import com.opensignal.protocol.gb20999.core.constant.DataClassId;
import com.opensignal.protocol.gb20999.core.frame.DataValue;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.core.frame.FrameType;
import com.opensignal.protocol.gb20999.server.Gb20999ServerConfig;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Broadcasts TRAP frames to all TCP clients registered by {@link ServerRequestHandler}.
 */
public class ServerTrapSender {

    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final Gb20999ServerConfig config;
    private final AtomicInteger trapSequence = new AtomicInteger(0);

    public ServerTrapSender(Gb20999ServerConfig config) {
        this.config = config;
    }

    public void register(Channel channel) {
        channels.add(channel);
    }

    public void unregister(Channel channel) {
        channels.remove(channel);
    }

    public void sendTrap(Frame trapFrame) {
        channels.writeAndFlush(trapFrame);
    }

    /**
     * Sends a minimal alarm-style TRAP using data class 15 (ALARM).
     */
    public void sendAlarmTrap(int alarmType, int alarmValue) {
        byte[] payload = new byte[4];
        payload[0] = (byte) ((alarmType >> 8) & 0xFF);
        payload[1] = (byte) (alarmType & 0xFF);
        payload[2] = (byte) ((alarmValue >> 8) & 0xFF);
        payload[3] = (byte) (alarmValue & 0xFF);

        DataValue dv = DataValue.of(1, DataClassId.ALARM, 1, 0, 0, payload);
        List<DataValue> list = new ArrayList<DataValue>(1);
        list.add(dv);

        int seq = trapSequence.incrementAndGet() & 0xFF;
        Frame trap = Frame.builder()
                .version(Frame.DEFAULT_VERSION)
                .upperId(config.getUpperId())
                .signalId(config.getSignalId())
                .crossId(config.getCrossId())
                .sequence(seq)
                .frameType(FrameType.TRAP)
                .dataValues(Collections.unmodifiableList(list))
                .build();
        sendTrap(trap);
    }
}
