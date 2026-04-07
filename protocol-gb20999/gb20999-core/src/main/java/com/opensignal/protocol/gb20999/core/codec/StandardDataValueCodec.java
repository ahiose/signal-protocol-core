package com.opensignal.protocol.gb20999.core.codec;

import com.opensignal.protocol.common.exception.CodecException;
import com.opensignal.protocol.common.spi.DataValueCodec;
import com.opensignal.protocol.common.spi.VendorProfile;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves the appropriate {@link DataValueCodec} for a given data attribute,
 * checking vendor overrides first, falling back to standard codecs.
 */
public final class StandardDataValueCodec {

    private static final Map<Long, DataValueCodec> STANDARD_CODECS = new ConcurrentHashMap<>();

    private StandardDataValueCodec() {}

    public static DataValueCodec resolve(VendorProfile vendor, int dataClassId, int attributeId) {
        DataValueCodec custom = vendor.customCodec(dataClassId, attributeId);
        if (custom != null) {
            return custom;
        }
        return STANDARD_CODECS.computeIfAbsent(key(dataClassId, attributeId), k -> new DefaultCodec());
    }

    private static long key(int dataClassId, int attributeId) {
        return ((long) dataClassId << 32) | (attributeId & 0xFFFFFFFFL);
    }

    /**
     * Default codec: reads/writes raw bytes as-is.
     */
    private static class DefaultCodec implements DataValueCodec {

        @Override
        public void encode(ByteBuffer buf, Object value) {
            if (value instanceof byte[]) {
                buf.put((byte[]) value);
            } else if (value instanceof Integer) {
                buf.put((byte) (((Integer) value) & 0xFF));
            } else if (value instanceof Long) {
                buf.putInt(((Long) value).intValue());
            } else {
                throw new CodecException("Unsupported value type: " + value.getClass().getName());
            }
        }

        @Override
        public Object decode(ByteBuffer buf, int length) {
            byte[] data = new byte[length];
            buf.get(data);
            return data;
        }
    }
}
