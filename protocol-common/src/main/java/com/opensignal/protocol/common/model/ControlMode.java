package com.opensignal.protocol.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard control modes as defined in the national standard.
 * Vendor-specific device codes are resolved through {@link com.opensignal.protocol.common.spi.VendorProfile}.
 */
@AllArgsConstructor
@Getter
public enum ControlMode {

    CLOSE_LAMP(11, "关灯"),
    ALL_RED(12, "全红控制"),
    YELLOW_FLASH(13, "黄闪控制"),
    LOCAL_FIXED_CYCLE(21, "单点多时段定时控制"),
    LOCAL_INDUCTION(22, "单点感应控制"),
    LOCAL_ADAPTIVE(23, "单点自适应"),
    LOCAL_HOLO_ADAPTIVE(24, "单点全息自适应"),
    BUS_PRIORITY(25, "公交优先"),
    CENTER_FIXED_CYCLE(26, "中心定周期"),
    LINE_COORDINATION(31, "线协调控制"),
    HOLO_LINE_ADAPTIVE(32, "全息干线自适应"),
    CENTER_LINE_COORDINATION(34, "中心干线协调控制"),
    LOCAL_COORDINATION(35, "本地协调控制"),
    AREA_COORDINATION(41, "区域协调控制"),
    CENTER_MANUAL(51, "干预控制手动控制"),
    CENTER_LOCK_STAGE(52, "干预控制锁定阶段"),
    CENTER_LOCK_SCHEME(53, "干预控制-指定方案"),
    SIGNAL_MANUAL(61, "信号机手动控制");

    private final int code;
    private final String description;

    private static final Map<Integer, ControlMode> CODE_MAP = new HashMap<>();

    static {
        for (ControlMode mode : values()) {
            CODE_MAP.put(mode.code, mode);
        }
    }

    public static ControlMode fromCode(int code) {
        return CODE_MAP.get(code);
    }
}
