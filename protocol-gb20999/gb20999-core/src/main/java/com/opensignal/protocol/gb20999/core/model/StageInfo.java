package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 6 — 相位阶段信息 (StageInfo).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageInfo {

    /** Object 1 — 实际阶段数 */
    private int actualCount;
    /** Object 2 — 阶段配置表 */
    private List<StageConfigRow> configTable;
    /** Object 3 — 阶段状态表 */
    private List<StageStatusRow> statusTable;
    /** Object 4 — 阶段控制表 */
    private List<StageControlRow> controlTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageConfigRow {
        private int stageId;
        /** 包含相位序列，协议长度 8 字节 */
        private byte[] phases;
        private byte[] lateStartTime;
        private byte[] earlyEndTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageStatusRow {
        private int stageId;
        private int status;
        private int elapsedTime;
        private int remainingTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageControlRow {
        private int stageId;
        private int softDemand;
        private int mask;
        private int prohibit;
    }
}
