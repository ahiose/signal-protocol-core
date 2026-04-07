package com.opensignal.protocol.gb20999.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Signal controller run modes as defined in A.2.11.
 */
public enum RunMode {

    CENTER_CONTROL(0x10, "中心控制模式"),
    CENTER_TIMETABLE(0x11, "中心日计划控制"),
    CENTER_OPTIMIZATION(0x12, "中心优化控制"),
    CENTER_COORDINATION(0x13, "中心协调控制"),
    CENTER_ADAPTIVE(0x14, "中心自适应控制"),
    CENTER_MANUAL(0x15, "中心手动控制"),

    LOCAL_CONTROL(0x20, "本地控制模式"),
    LOCAL_FIX_CYCLE(0x21, "本地定周期控制"),
    LOCAL_VA(0x22, "本地感应控制"),
    LOCAL_COORDINATION(0x23, "本地协调控制"),
    LOCAL_ADAPTIVE(0x24, "本地自适应控制"),
    LOCAL_MANUAL(0x25, "本地手动控制"),

    SPECIAL_CONTROL(0x30, "特殊控制"),
    SPECIAL_FLASH(0x31, "黄闪控制"),
    SPECIAL_ALL_RED(0x32, "全红控制"),
    SPECIAL_ALL_OFF(0x33, "关灯控制");

    private final int code;
    private final String description;

    private static final Map<Integer, RunMode> CODE_MAP = new HashMap<>();

    static {
        for (RunMode mode : values()) {
            CODE_MAP.put(mode.code, mode);
        }
    }

    RunMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static RunMode fromCode(int code) {
        return CODE_MAP.get(code);
    }

    public boolean isCenterMode() {
        return code >= 0x10 && code <= 0x1F;
    }

    public boolean isLocalMode() {
        return code >= 0x20 && code <= 0x2F;
    }

    public boolean isSpecialMode() {
        return code >= 0x30 && code <= 0x3F;
    }
}
