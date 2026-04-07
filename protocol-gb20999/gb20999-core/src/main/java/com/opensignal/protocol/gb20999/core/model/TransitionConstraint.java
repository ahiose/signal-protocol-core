package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 10 — 过渡约束 (TransitionConstraint).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransitionConstraint {

    /** Object 1 — 约束表 */
    private List<ConstraintRow> constraintTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConstraintRow {
        private int targetStageId;
        /** 约束值，协议长度 64 字节 */
        private byte[] constraintValue;
    }
}
