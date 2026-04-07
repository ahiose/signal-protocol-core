package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class 16 — 故障数据 (FaultData).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaultData {

    /** Object 1 — 当前故障条数 */
    private int currentFaultCount;
    /** Object 2 — 故障表 */
    private List<FaultRow> faultTable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaultRow {
        private int faultId;
        private int faultType;
        private byte[] faultTime;
        private int faultAction;
    }
}
