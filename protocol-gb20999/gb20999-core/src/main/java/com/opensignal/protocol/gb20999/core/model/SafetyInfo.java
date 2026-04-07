package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 7 — 相位安全信息 (SafetyInfo).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SafetyInfo {

    /** Object 1 — 冲突表 */
    private List<ConflictRow> conflictTable;
    /** Object 2 — 间隔表 */
    private List<IntergreenRow> intergreenTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConflictRow {
        private int phaseId;
        /** 冲突相位位图，协议长度 8 字节 */
        private byte[] conflictPhases;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IntergreenRow {
        private int phaseId;
        /** 间隔时间矩阵，协议长度 64 字节 */
        private byte[] intergreenTimes;
    }
}
