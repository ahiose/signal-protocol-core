package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 5 — 检测器信息 (DetectorInfo).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectorInfo {

    /** Object 1 — 实际检测器数 */
    private int actualCount;
    /** Object 2 — 检测器配置表 */
    private List<DetectorConfigRow> configTable;
    /** Object 3 — 检测器状态表 */
    private List<DetectorStatusRow> statusTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetectorConfigRow {
        private int detectorId;
        private int type;
        private int flowCycle;
        private int occupancyCycle;
        private int position;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetectorStatusRow {
        private int detectorId;
        private int vehiclePresent;
        private int speed;
        private int vehicleType;
        private String plate;
        private int queueLength;
    }
}
