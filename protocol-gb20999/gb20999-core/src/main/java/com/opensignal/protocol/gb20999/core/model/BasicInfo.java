package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class 2 — 基础信息 (BasicInfo).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicInfo {

    /** Object 1 — 信号机安装路口 */
    private String crossName;
    /** Object 2 — IPv4 配置 */
    private Ipv4Config ipv4Config;
    /** Object 3 — 上级 IPv4 配置 */
    private UpperIpv4Config upperIpv4Config;
    /** Object 4 — 所属时区 */
    private int timezone;
    /** Object 5 — 信号机 ID */
    private int signalId;
    /** Object 6 — 控制路口数量 */
    private int crossCount;
    /** Object 7 — GPS 时钟标志 */
    private int gpsClockFlag;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ipv4Config {
        private String ip;
        private String subnet;
        private String gateway;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpperIpv4Config {
        private String ip;
        private int port;
        private int commType;
    }
}
