package com.opensignal.protocol.gb20999.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Command codes as defined in A.2.16.
 */
public enum CommandCode {

    CANCEL(0x00, "取消命令"),
    FLASH(0x01, "黄闪"),
    ALL_RED(0x02, "全红"),
    ON(0x03, "开灯"),
    OFF(0x04, "关灯"),
    RESET(0x05, "重启");

    private final int code;
    private final String description;

    private static final Map<Integer, CommandCode> CODE_MAP = new HashMap<>();

    static {
        for (CommandCode c : values()) {
            CODE_MAP.put(c.code, c);
        }
    }

    CommandCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static CommandCode fromCode(int code) {
        return CODE_MAP.get(code);
    }
}
