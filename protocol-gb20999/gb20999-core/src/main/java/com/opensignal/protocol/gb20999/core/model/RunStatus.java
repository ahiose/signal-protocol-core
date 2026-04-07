package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 13 — 运行状态 (RunStatus).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunStatus {

    /** Object 1 — 设备状态 */
    private DeviceStatus deviceStatus;
    /** Object 2 — 路口运行状态表 */
    private List<RunStatusRow> runStatusTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceStatus {
        private int detectorStatus;
        private int moduleStatus;
        private int doorStatus;
        private int voltage;
        private int current;
        private int temperature;
        private int humidity;
        private int water;
        private int smoke;
        private byte[] standardTime;
        private byte[] localTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RunStatusRow {
        private int crossIndex;
        private int runMode;
        private int currentPlan;
        private int currentStage;
    }
}
