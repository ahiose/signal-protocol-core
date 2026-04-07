package com.opensignal.protocol.gb20999.core.frame;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Represents a complete GB/T 20999 protocol frame.
 *
 * <p>Wire format (A.1):
 * <pre>
 * | Start(1) | Length(2) | Version(2) | UpperId(1) | SignalId(4) | CrossId(1) |
 * | Seq(1)   | Type(1)   | Count(1)   | DataValues(variable)     |
 * | CRC16(2) | End(1)    |
 * </pre>
 */
@Getter
@Builder
public class Frame {

    public static final byte START_BYTE = 0x7E;
    public static final byte END_BYTE = 0x7D;
    public static final byte ESCAPE_BYTE = 0x5C;
    public static final int DEFAULT_VERSION = 0x0102;

    private final int version;
    private final int upperId;
    private final int signalId;
    private final int crossId;
    private final int sequence;
    private final FrameType frameType;
    private final List<DataValue> dataValues;

    public int dataValueCount() {
        return dataValues == null ? 0 : dataValues.size();
    }

    public DataValue getDataValue(int index) {
        return dataValues.get(index);
    }

    /**
     * Build a query frame for the given data value addresses (no payload data).
     */
    public static Frame query(int upperId, int signalId, int crossId, int sequence,
                              List<DataValue> dataValues) {
        return Frame.builder()
                .version(DEFAULT_VERSION)
                .upperId(upperId)
                .signalId(signalId)
                .crossId(crossId)
                .sequence(sequence)
                .frameType(FrameType.QUERY)
                .dataValues(dataValues)
                .build();
    }

    /**
     * Build a set frame with payload data.
     */
    public static Frame set(int upperId, int signalId, int crossId, int sequence,
                            List<DataValue> dataValues) {
        return Frame.builder()
                .version(DEFAULT_VERSION)
                .upperId(upperId)
                .signalId(signalId)
                .crossId(crossId)
                .sequence(sequence)
                .frameType(FrameType.SET)
                .dataValues(dataValues)
                .build();
    }

    /**
     * Build a heartbeat query frame.
     */
    public static Frame heartbeat(int upperId, int signalId, int crossId, int sequence) {
        return Frame.builder()
                .version(DEFAULT_VERSION)
                .upperId(upperId)
                .signalId(signalId)
                .crossId(crossId)
                .sequence(sequence)
                .frameType(FrameType.HEARTBEAT_QUERY)
                .dataValues(Collections.emptyList())
                .build();
    }
}
