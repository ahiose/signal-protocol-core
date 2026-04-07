package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class 1 — 设备信息 (DeviceInfo).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {

    /** Object 1 — 制造厂商 */
    private String manufacturer;
    /** Object 2 — 设备版本 */
    private String deviceVersion;
    /** Object 3 — 设备编号 */
    private String deviceSerial;
    /** Object 4 — 出厂日期 */
    private String manufactureDate;
    /** Object 5 — 配置日期 (QS) */
    private String configDate;
}
