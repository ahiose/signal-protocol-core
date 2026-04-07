package com.opensignal.protocol.common.exception;

public class ConnectionException extends ProtocolException {

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
