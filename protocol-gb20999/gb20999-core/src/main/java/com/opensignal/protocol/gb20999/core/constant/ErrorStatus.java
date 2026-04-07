package com.opensignal.protocol.gb20999.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Error status codes as defined in A.2.5.
 */
public enum ErrorStatus {

    BAD_VALUE(0x10, "值错误"),
    WRONG_LENGTH(0x11, "值长度错误"),
    OVERFLOW(0x12, "值越界"),
    READ_ONLY(0x20, "值只读"),
    NULL_VALUE(0x30, "值不存在"),
    GENERAL_ERROR(0x40, "值一般错误"),
    CONTROL_FAIL(0x50, "控制失败");

    private final int code;
    private final String description;

    private static final Map<Integer, ErrorStatus> CODE_MAP = new HashMap<>();

    static {
        for (ErrorStatus s : values()) {
            CODE_MAP.put(s.code, s);
        }
    }

    ErrorStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static ErrorStatus fromCode(int code) {
        return CODE_MAP.get(code);
    }
}
