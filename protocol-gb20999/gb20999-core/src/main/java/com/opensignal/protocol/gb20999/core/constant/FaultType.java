package com.opensignal.protocol.gb20999.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Fault types as defined in A.2.14.
 */
public enum FaultType {

    GREEN_CONFLICT(0x10, "绿冲突故障"),
    GREEN_RED_CONFLICT(0x11, "红绿冲突故障"),
    RED_LIGHT(0x20, "红灯故障"),
    YELLOW_LIGHT(0x21, "黄灯故障"),
    GREEN_LIGHT(0x22, "绿灯故障"),
    COMMUNICATION(0x30, "通信故障"),
    SELF_CHECK(0x40, "自检故障"),
    DETECTOR(0x41, "检测器故障"),
    RELAY(0x42, "继电器故障"),
    MEMORY(0x43, "存储器故障"),
    CLOCK(0x44, "时钟故障"),
    MOTHERBOARD(0x45, "主板故障"),
    PHASE_BOARD(0x46, "相位板故障"),
    DETECTOR_BOARD(0x47, "检测板故障"),
    CONFIG(0x50, "配置故障"),
    RESPONSE(0x70, "控制响应故障");

    private final int code;
    private final String description;

    private static final Map<Integer, FaultType> CODE_MAP = new HashMap<>();

    static {
        for (FaultType t : values()) {
            CODE_MAP.put(t.code, t);
        }
    }

    FaultType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static FaultType fromCode(int code) {
        return CODE_MAP.get(code);
    }
}
