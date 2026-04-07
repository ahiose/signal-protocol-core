package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 11 — 日计划 (TimeTable).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeTable {

    /** Object 1 — 实际日计划数 */
    private int actualCount;
    /** Object 2 — 日计划配置表 */
    private List<TimeTableConfigRow> configTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeTableConfigRow {
        private int timeTableId;
        private int crossIndex;
        private byte[] startTimeChain;
        private byte[] planChain;
        private byte[] modeChain;
        private byte[] actionChain1;
        private byte[] actionChain2;
        private byte[] actionChain3;
        private byte[] actionChain4;
        private byte[] actionChain5;
        private byte[] actionChain6;
        private byte[] actionChain7;
        private byte[] actionChain8;
    }
}
