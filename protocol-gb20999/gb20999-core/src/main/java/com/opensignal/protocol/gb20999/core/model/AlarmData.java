package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 15 — 报警数据 (AlarmData).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmData {

    /** Object 1 — 当前报警条数 */
    private int currentAlarmCount;
    /** Object 2 — 报警表 */
    private List<AlarmRow> alarmTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlarmRow {
        private int alarmId;
        private int alarmType;
        private int alarmValue;
        private byte[] alarmTime;
    }
}
