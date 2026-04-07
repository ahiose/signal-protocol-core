package com.opensignal.protocol.common.spi;

import java.nio.ByteBuffer;

/**
 * Custom encoder/decoder for a specific data attribute.
 *
 * <p>Vendors may override the standard encoding for certain attributes
 * when their signal controller deviates from the national standard.
 */
public interface DataValueCodec {

    void encode(ByteBuffer buf, Object value);

    Object decode(ByteBuffer buf, int length);
}
