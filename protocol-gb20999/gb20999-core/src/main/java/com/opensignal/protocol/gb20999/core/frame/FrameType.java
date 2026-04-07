package com.opensignal.protocol.gb20999.core.frame;

import java.util.HashMap;
import java.util.Map;

/**
 * GB/T 20999 frame types as defined in A.2.4.
 */
public enum FrameType {

    QUERY(0x10, "查询"),
    QUERY_REPLY(0x20, "查询应答"),
    QUERY_ERROR_REPLY(0x21, "查询出错回复"),
    SET(0x30, "设置"),
    SET_REPLY(0x40, "设置应答"),
    SET_ERROR_REPLY(0x41, "设置出错回复"),
    BROADCAST(0x50, "广播"),
    TRAP(0x60, "主动上报"),
    HEARTBEAT_QUERY(0x70, "心跳查询"),
    HEARTBEAT_REPLY(0x80, "心跳应答");

    private final int code;
    private final String description;

    private static final Map<Integer, FrameType> CODE_MAP = new HashMap<>();

    static {
        for (FrameType type : values()) {
            CODE_MAP.put(type.code, type);
        }
    }

    FrameType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    public static FrameType fromCode(int code) {
        FrameType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Unknown frame type: 0x" + Integer.toHexString(code));
        }
        return type;
    }

    public boolean isQuery() {
        return this == QUERY;
    }

    public boolean isResponse() {
        return this == QUERY_REPLY || this == SET_REPLY;
    }

    public boolean isError() {
        return this == QUERY_ERROR_REPLY || this == SET_ERROR_REPLY;
    }

    public boolean isHeartbeat() {
        return this == HEARTBEAT_QUERY || this == HEARTBEAT_REPLY;
    }
}
