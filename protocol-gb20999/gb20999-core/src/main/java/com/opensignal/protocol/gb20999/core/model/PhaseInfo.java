package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 4 — 相位信息 (PhaseInfo).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhaseInfo {

    /** Object 1 — 实际相位数 */
    private int actualCount;
    /** Object 2 — 相位配置表 */
    private List<PhaseConfigRow> configTable;
    /** Object 3 — 相位控制表 */
    private List<PhaseControlRow> controlTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhaseTransition {
        private int type;
        private int time;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhaseConfigRow {
        private int phaseId;
        /** 灯组映射，协议长度 8 字节 */
        private byte[] lightGroups;
        private List<PhaseTransition> loseRightTransitions;
        private List<PhaseTransition> gainRightTransitions;
        private List<PhaseTransition> startupGainTransitions;
        private List<PhaseTransition> startupLoseTransitions;
        private int minGreen;
        private int maxGreen1;
        private int maxGreen2;
        private int extendGreen;
        /** 需求字段，协议长度 8 字节 */
        private byte[] demand;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhaseControlRow {
        private int phaseId;
        private int mask;
        private int prohibit;
    }
}
