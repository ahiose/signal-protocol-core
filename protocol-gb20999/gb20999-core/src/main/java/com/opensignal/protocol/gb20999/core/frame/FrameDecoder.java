package com.opensignal.protocol.gb20999.core.frame;

import com.opensignal.protocol.common.exception.CodecException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Decodes raw bytes (after start/end byte stripping) into a {@link Frame}.
 *
 * <p>Expects input WITHOUT start byte (0x7E) and end byte (0x7D).
 * Escape characters should be removed before calling this decoder
 * (for TCP/RS232 transports).
 */
public final class FrameDecoder {

    private FrameDecoder() {}

    /**
     * @param raw frame bytes without start/end markers, already unescaped
     */
    public static Frame decode(byte[] raw) {
        if (raw.length < 13) {
            throw new CodecException("Frame too short: " + raw.length + " bytes");
        }

        int crcOffset = raw.length - 2;
        int expectedCrc = ((raw[crcOffset] & 0xFF) << 8) | (raw[crcOffset + 1] & 0xFF);
        if (!CRC16.verify(raw, 0, crcOffset, expectedCrc)) {
            throw new CodecException("CRC16 mismatch: expected 0x"
                    + Integer.toHexString(expectedCrc) + ", computed 0x"
                    + Integer.toHexString(CRC16.calculate(raw, 0, crcOffset)));
        }

        ByteBuffer buf = ByteBuffer.wrap(raw, 0, crcOffset);

        int length = buf.getShort() & 0xFFFF;
        int version = buf.getShort() & 0xFFFF;
        int upperId = buf.get() & 0xFF;
        int signalId = buf.getInt();
        int crossId = buf.get() & 0xFF;
        int sequence = buf.get() & 0xFF;
        FrameType frameType = FrameType.fromCode(buf.get() & 0xFF);

        List<DataValue> dataValues;
        if (frameType.hasDataValues()) {
            int dataValueCount = buf.get() & 0xFF;
            dataValues = new ArrayList<>(dataValueCount);
            for (int i = 0; i < dataValueCount; i++) {
                int dvIndex = buf.get() & 0xFF;
                int dvLength = buf.get() & 0xFF;
                int dataClassId = buf.get() & 0xFF;
                int objectId = buf.get() & 0xFF;
                int attributeId = buf.get() & 0xFF;
                int elementId = buf.get() & 0xFF;

                int dataLen = dvLength - 4;
                byte[] data = null;
                if (dataLen > 0) {
                    data = new byte[dataLen];
                    buf.get(data);
                }

                dataValues.add(new DataValue(dvIndex, dvLength, dataClassId, objectId, attributeId, elementId, data));
            }
        } else {
            dataValues = Collections.emptyList();
        }

        return Frame.builder()
                .version(version)
                .upperId(upperId)
                .signalId(signalId)
                .crossId(crossId)
                .sequence(sequence)
                .frameType(frameType)
                .dataValues(dataValues)
                .build();
    }
}
