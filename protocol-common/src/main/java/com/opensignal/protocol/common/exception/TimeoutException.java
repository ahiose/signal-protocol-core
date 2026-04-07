package com.opensignal.protocol.common.exception;

public class TimeoutException extends ProtocolException {

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String connectionId, long timeoutMs) {
        super("Response timeout on connection [" + connectionId + "] after " + timeoutMs + "ms");
    }
}
