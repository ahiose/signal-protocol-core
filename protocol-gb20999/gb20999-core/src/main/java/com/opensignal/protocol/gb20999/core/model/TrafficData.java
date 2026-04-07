package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 14 — 交通数据 (TrafficData).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficData {

    /** Object 1 — 实时数据 */
    private byte[] realtimeData;
    /** Object 2 — 统计表 */
    private List<TrafficStatisticsRow> statisticsTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrafficStatisticsRow {
        private int detectorId;
        private int flow;
        private int occupancy;
        private int avgSpeed;
    }
}
