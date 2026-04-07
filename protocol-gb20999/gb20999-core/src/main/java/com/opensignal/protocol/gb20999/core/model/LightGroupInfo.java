package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 3 — 灯组信息 (LightGroupInfo).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LightGroupInfo {

    /** Object 1 — 实际灯组数 */
    private int actualCount;
    /** Object 2 — 灯组配置表 */
    private List<LightGroupConfigRow> configTable;
    /** Object 3 — 灯组状态表 */
    private List<LightStatusRow> statusTable;
    /** Object 4 — 灯组控制表 */
    private List<LightGroupControlRow> controlTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LightGroupConfigRow {
        private int lightId;
        private int lightType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LightStatusRow {
        private int lightStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LightGroupControlRow {
        private int lightId;
        private int mask;
        private int prohibit;
    }
}
