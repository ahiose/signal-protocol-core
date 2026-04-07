package com.opensignal.protocol.gb20999.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Light group status codes as defined in A.2.7.
 */
public enum LightStatus {

    OFF(0x01, "灭灯"),
    RED(0x10, "红灯"),
    RED_FLASH(0x11, "红闪"),
    RED_FAST_FLASH(0x12, "红快闪"),
    GREEN(0x20, "绿灯"),
    GREEN_FLASH(0x21, "绿闪"),
    GREEN_FAST_FLASH(0x22, "绿快闪"),
    YELLOW(0x30, "黄灯"),
    YELLOW_FLASH(0x31, "黄闪"),
    YELLOW_FAST_FLASH(0x32, "黄快闪"),
    RED_YELLOW(0x40, "红黄灯");

    private final int code;
    private final String description;

    private static final Map<Integer, LightStatus> CODE_MAP = new HashMap<>();

    static {
        for (LightStatus s : values()) {
            CODE_MAP.put(s.code, s);
        }
    }

    LightStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static LightStatus fromCode(int code) {
        return CODE_MAP.get(code);
    }
}
