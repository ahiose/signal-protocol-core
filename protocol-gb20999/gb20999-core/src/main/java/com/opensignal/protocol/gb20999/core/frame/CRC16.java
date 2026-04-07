package com.opensignal.protocol.gb20999.core.frame;

/**
 * CRC16 calculation per GB/T 20999: polynomial x^16 + x^12 + x^2 + 1 (0x1021).
 *
 * <p>Escape characters are NOT included in CRC computation.
 */
public final class CRC16 {

    private static final int POLYNOMIAL = 0x1021;
    private static final int INITIAL = 0xFFFF;

    private CRC16() {}

    public static int calculate(byte[] data) {
        return calculate(data, 0, data.length);
    }

    public static int calculate(byte[] data, int offset, int length) {
        int crc = INITIAL;
        for (int i = offset; i < offset + length; i++) {
            crc ^= (data[i] & 0xFF) << 8;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc <<= 1;
                }
            }
            crc &= 0xFFFF;
        }
        return crc;
    }

    public static boolean verify(byte[] data, int offset, int length, int expected) {
        return calculate(data, offset, length) == expected;
    }
}
