package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 17 — 中心控制 (CenterControl).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CenterControl {

    /** Object 1 — 中心控制表 */
    private List<CenterControlRow> controlTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CenterControlRow {
        private int crossId;
        private int designatedStage;
        private int designatedPlan;
        private int designatedRunMode;
    }
}
