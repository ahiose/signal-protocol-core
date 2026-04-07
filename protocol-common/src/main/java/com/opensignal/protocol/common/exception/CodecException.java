package com.opensignal.protocol.common.exception;

public class CodecException extends ProtocolException {

    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
