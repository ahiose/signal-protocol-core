package com.opensignal.protocol.gb20999.core.constant;

/**
 * Data class IDs as defined in GB/T 20999 Appendix B.
 */
public final class DataClassId {

    private DataClassId() {}

    public static final int DEVICE = 1;              // C.1  设备
    public static final int PHASE = 2;               // C.2  相位
    public static final int LIGHT_GROUP = 3;          // C.3  信号灯组
    public static final int PHASE_INTERGREEN = 4;     // C.4  绿间隔
    public static final int DETECTOR = 5;             // C.5  检测器
    public static final int PHASE_STAGE = 6;          // C.6  相位阶段
    public static final int PHASE_STAGE_CHAIN = 7;    // C.7  相位阶段链
    public static final int PLAN = 8;                 // C.8  方案
    public static final int TIME_TABLE = 9;           // C.9  日计划
    public static final int DATE_SCHEDULE = 10;       // C.10 调度表
    public static final int COORDINATION = 11;        // C.11 协调控制
    public static final int PRIORITY = 12;            // C.12 优先/紧急
    public static final int RUN_STATUS = 13;          // C.13 运行状态
    public static final int TRAFFIC_DATA = 14;        // C.14 交通数据
    public static final int ALARM = 15;               // C.15 报警数据
    public static final int FAULT = 16;               // C.16 故障数据
    public static final int CENTER_CONTROL = 17;      // C.17 中心控制
    public static final int COMMAND = 18;             // C.18 命令管道
}
