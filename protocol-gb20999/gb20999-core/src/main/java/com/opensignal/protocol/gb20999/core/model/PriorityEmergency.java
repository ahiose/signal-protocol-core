package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 8 — 紧急优先 (PriorityEmergency).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityEmergency {

    /** Object 1 — 实际优先请求数 */
    private int actualPriorityCount;
    /** Object 2 — 优先配置表 */
    private List<PriorityConfigRow> priorityConfigTable;
    /** Object 3 — 优先状态表 */
    private List<PriorityStatusRow> priorityStatusTable;
    /** Object 4 — 实际紧急请求数 */
    private int actualEmergencyCount;
    /** Object 5 — 紧急配置表 */
    private List<EmergencyConfigRow> emergencyConfigTable;
    /** Object 6 — 紧急状态表 */
    private List<EmergencyStatusRow> emergencyStatusTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityConfigRow {
        private int priorityId;
        private int requestStage;
        private int level;
        private int mask;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityStatusRow {
        private int priorityId;
        private int requestStatus;
        private int execStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyConfigRow {
        private int emergencyId;
        private int requestStage;
        private int level;
        private int mask;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyStatusRow {
        private int emergencyId;
        private int requestStatus;
        private int execStatus;
    }
}
