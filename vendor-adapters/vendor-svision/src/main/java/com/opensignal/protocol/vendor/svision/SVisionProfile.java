package com.opensignal.protocol.vendor.svision;

import com.opensignal.protocol.common.spi.ConnectionStrategy;
import com.opensignal.protocol.common.spi.VendorProfile;

import java.util.HashMap;
import java.util.Map;

/**
 * SVision (圣维信) signal controller vendor profile.
 */
public class SVisionProfile implements VendorProfile {

    private static final Map<Integer, Integer> STANDARD_TO_DEVICE = new HashMap<>();
    private static final Map<Integer, Integer> DEVICE_TO_STANDARD = new HashMap<>();

    static {
        mapMode(32, 21);   // 全息干线自适应
        mapMode(34, 21);   // 中心干线协调控制
        mapMode(51, 21);   // 干预控制手动控制
        mapMode(52, 21);   // 干预控制锁定阶段
        mapMode(53, 19);   // 干预控制-指定方案
        mapMode(22, 34);   // 单点感应控制
        mapMode(41, 35);   // 区域协调控制
        mapMode(31, 35);   // 线协调控制
        mapMode(23, 36);   // 单点自适应
        mapMode(24, 21);   // 单点全息自适应
        mapMode(26, 21);   // 中心定周期
        mapMode(25, 21);   // 公交优先
        mapMode(13, 49);   // 黄闪控制
        mapMode(12, 50);   // 全红控制
        mapMode(11, 51);   // 关灯
        mapMode(21, 33);   // 单点多时段定时控制
        mapMode(35, 35);   // 本地协调控制 / 降级模式
        mapMode(61, 21);   // 信号机手动控制
    }

    private static void mapMode(int standard, int device) {
        STANDARD_TO_DEVICE.put(standard, device);
        DEVICE_TO_STANDARD.putIfAbsent(device, standard);
    }

    @Override
    public String vendorId() {
        return "svision";
    }

    @Override
    public String vendorName() {
        return "圣维信";
    }

    @Override
    public int toDeviceControlMode(int standardMode) {
        return STANDARD_TO_DEVICE.getOrDefault(standardMode, standardMode);
    }

    @Override
    public int toStandardControlMode(int deviceMode) {
        return DEVICE_TO_STANDARD.getOrDefault(deviceMode, deviceMode);
    }

    @Override
    public ConnectionStrategy connectionStrategy() {
        return ConnectionStrategy.UDP;
    }
}
