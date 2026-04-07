package com.opensignal.protocol.common.spi;

/**
 * Intercepts command send/receive for vendor-specific pre/post processing.
 *
 * <p>Typical uses:
 * <ul>
 *   <li>Vendor-specific handshake before center control</li>
 *   <li>Polling for control mode confirmation after command</li>
 *   <li>Automatic downgrade task scheduling</li>
 * </ul>
 */
public interface CommandInterceptor {

    CommandInterceptor NOOP = new CommandInterceptor() {};

    default InterceptResult beforeSend(byte[] frameBytes, InterceptContext ctx) {
        return InterceptResult.CONTINUE;
    }

    default InterceptResult afterResponse(byte[] requestBytes, byte[] responseBytes, InterceptContext ctx) {
        return InterceptResult.CONTINUE;
    }

    enum InterceptResult {
        CONTINUE,
        ABORT,
        RETRY
    }

    interface InterceptContext {
        String connectionId();
        String vendorId();
        void setAttribute(String key, Object value);
        Object getAttribute(String key);
    }
}
