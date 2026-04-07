package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 9 — 方案信息 (PlanInfo).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanInfo {

    /** Object 1 — 实际方案数 */
    private int actualCount;
    /** Object 2 — 方案配置表 */
    private List<PlanConfigRow> configTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanConfigRow {
        private int planId;
        private int crossIndex;
        private int cycle;
        private int coordIndex;
        private int offset;
        /** 阶段链，协议长度 16 字节 */
        private byte[] stageChain;
        /** 阶段时间链，协议长度 32 字节 */
        private byte[] stageTimeChain;
        /** 阶段类型链，协议长度 16 字节 */
        private byte[] stageTypeChain;
    }
}
