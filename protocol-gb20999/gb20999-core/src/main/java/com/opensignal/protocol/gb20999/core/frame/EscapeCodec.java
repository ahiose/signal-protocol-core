package com.opensignal.protocol.gb20999.core.frame;

import java.io.ByteArrayOutputStream;

/**
 * Handles escape character encoding/decoding for TCP and RS232 transports.
 *
 * <p>Per A.1: when using TCP/RS232, bytes 0x7E, 0x7D, and 0x5C within
 * the payload must be preceded by the escape character 0x5C.
 * UDP does not use escape characters.
 */
public final class EscapeCodec {

    private EscapeCodec() {}

    /**
     * Escape special bytes in the frame body (between start and end markers).
     */
    public static byte[] escape(byte[] raw) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(raw.length + 16);
        for (byte b : raw) {
            if (b == Frame.START_BYTE || b == Frame.END_BYTE || b == Frame.ESCAPE_BYTE) {
                out.write(Frame.ESCAPE_BYTE);
            }
            out.write(b);
        }
        return out.toByteArray();
    }

    /**
     * Remove escape characters, restoring the original byte sequence.
     */
    public static byte[] unescape(byte[] escaped) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(escaped.length);
        boolean escapeNext = false;
        for (byte b : escaped) {
            if (escapeNext) {
                out.write(b);
                escapeNext = false;
            } else if (b == Frame.ESCAPE_BYTE) {
                escapeNext = true;
            } else {
                out.write(b);
            }
        }
        return out.toByteArray();
    }
}
