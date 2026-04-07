package com.opensignal.protocol.gb20999.core.frame;

import com.opensignal.protocol.common.exception.CodecException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Encodes a {@link Frame} into raw bytes ready for transmission.
 *
 * <p>The caller decides whether to apply escape encoding (TCP/RS232)
 * or skip it (UDP).
 */
public final class FrameEncoder {

    private FrameEncoder() {}

    public static byte[] encode(Frame frame, boolean applyEscape) {
        try {
            byte[] body = encodeBody(frame);
            int crc = CRC16.calculate(body);

            if (applyEscape) {
                body = EscapeCodec.escape(body);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream(body.length + 5);
            out.write(Frame.START_BYTE);
            out.write(body);
            if (applyEscape) {
                byte[] crcBytes = EscapeCodec.escape(new byte[]{
                        (byte) ((crc >> 8) & 0xFF),
                        (byte) (crc & 0xFF)
                });
                out.write(crcBytes);
            } else {
                out.write((crc >> 8) & 0xFF);
                out.write(crc & 0xFF);
            }
            out.write(Frame.END_BYTE);
            return out.toByteArray();
        } catch (IOException e) {
            throw new CodecException("Frame encoding failed", e);
        }
    }

    private static byte[] encodeBody(Frame frame) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
        DataOutputStream dos = new DataOutputStream(baos);
        boolean hasDataValues = frame.getFrameType().hasDataValues();

        int bodyLength = calculateBodyLength(frame);
        dos.writeShort(bodyLength);
        dos.writeShort(frame.getVersion());
        dos.writeByte(frame.getUpperId());
        dos.writeInt(frame.getSignalId());
        dos.writeByte(frame.getCrossId());
        dos.writeByte(frame.getSequence());
        dos.writeByte(frame.getFrameType().code());

        if (hasDataValues) {
            dos.writeByte(frame.dataValueCount());
            for (DataValue dv : frame.getDataValues()) {
                dos.writeByte(dv.getIndex());
                dos.writeByte(dv.wireLength());
                dos.writeByte(dv.getDataClassId());
                dos.writeByte(dv.getObjectId());
                dos.writeByte(dv.getAttributeId());
                dos.writeByte(dv.getElementId());
                if (dv.hasData()) {
                    dos.write(dv.getData());
                }
            }
        }

        dos.flush();
        return baos.toByteArray();
    }

    private static int calculateBodyLength(Frame frame) {
        int headerLen = 2 + 1 + 4 + 1 + 1 + 1; // version(2)+upperId(1)+signalId(4)+crossId(1)+seq(1)+type(1)
        if (!frame.getFrameType().hasDataValues()) {
            return headerLen;
        }
        headerLen += 1; // count(1)
        int dvLen = 0;
        for (DataValue dv : frame.getDataValues()) {
            dvLen += 2 + dv.wireLength(); // index(1) + length(1) + wireLength
        }
        return headerLen + dvLen;
    }
}
