package com.opensignal.protocol.gb20999.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Alarm types as defined in A.2.12.
 */
public enum AlarmType {

    LIGHT(0x10, "信号灯报警"),
    DETECTOR(0x30, "检测器报警"),
    DEVICE(0x40, "设备故障报警"),
    ENVIRONMENT(0x60, "工作环境异常报警");

    private final int code;
    private final String description;

    private static final Map<Integer, AlarmType> CODE_MAP = new HashMap<>();

    static {
        for (AlarmType t : values()) {
            CODE_MAP.put(t.code, t);
        }
    }

    AlarmType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static AlarmType fromCode(int code) {
        return CODE_MAP.get(code);
    }
}
