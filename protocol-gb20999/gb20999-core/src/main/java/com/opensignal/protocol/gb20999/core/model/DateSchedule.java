package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 12 — 调度表 (DateSchedule).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateSchedule {

    /** Object 1 — 实际调度表项数 */
    private int actualCount;
    /** Object 2 — 调度配置表 */
    private List<ScheduleConfigRow> configTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleConfigRow {
        private int scheduleId;
        private int crossIndex;
        private int priority;
        private int weekValue;
        private int monthValue;
        private int dateValue;
        private int timeTableId;
    }
}
