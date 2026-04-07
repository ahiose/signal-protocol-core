package com.opensignal.protocol.gb20999.core.codec;

import com.opensignal.protocol.gb20999.core.frame.DataValue;

import java.nio.charset.StandardCharsets;

/**
 * Helpers for building {@link DataValue} instances and reading/writing primitive
 * fields from protocol byte arrays (big-endian).
 */
public final class DataValueMapper {

    private DataValueMapper() {}

    /**
     * Wraps raw payload bytes in a {@link DataValue} with the given index and four-tuple.
     */
    public static DataValue toDataValue(int index, int dataClassId, int objectId,
                                        int attributeId, int elementId, byte[] data) {
        return DataValue.of(index, dataClassId, objectId, attributeId, elementId, data);
    }

    public static int readUint8(byte[] data, int offset) {
        return data[offset] & 0xFF;
    }

    public static int readUint16(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }

    public static long readUint32(byte[] data, int offset) {
        return ((long) (data[offset] & 0xFF) << 24)
                | ((data[offset + 1] & 0xFF) << 16)
                | ((data[offset + 2] & 0xFF) << 8)
                | (data[offset + 3] & 0xFF);
    }

    public static void writeUint8(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
    }

    public static void writeUint16(byte[] data, int offset, int value) {
        data[offset] = (byte) ((value >> 8) & 0xFF);
        data[offset + 1] = (byte) (value & 0xFF);
    }

    public static void writeUint32(byte[] data, int offset, long value) {
        data[offset] = (byte) ((value >> 24) & 0xFF);
        data[offset + 1] = (byte) ((value >> 16) & 0xFF);
        data[offset + 2] = (byte) ((value >> 8) & 0xFF);
        data[offset + 3] = (byte) (value & 0xFF);
    }

    /**
     * Decodes {@code length} bytes as a string using ISO-8859-1 (single-byte, preserves raw octets).
     */
    public static String readString(byte[] data, int offset, int length) {
        return new String(data, offset, length, StandardCharsets.ISO_8859_1);
    }
}
