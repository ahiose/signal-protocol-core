package com.opensignal.protocol.vendor.keli;

import com.opensignal.protocol.common.spi.ConnectionStrategy;
import com.opensignal.protocol.common.spi.VendorProfile;

import java.util.HashMap;
import java.util.Map;

/**
 * KeLi signal controller vendor profile.
 *
 * <p>Provides control mode mapping between the national standard
 * and KeLi's proprietary signal controller codes.
 */
public class KeLiProfile implements VendorProfile {

    private static final Map<Integer, Integer> STANDARD_TO_DEVICE = new HashMap<>();
    private static final Map<Integer, Integer> DEVICE_TO_STANDARD = new HashMap<>();

    static {
        mapMode(53, 19);   // 干预控制-指定方案
        mapMode(51, 67);   // 干预控制手动控制
        mapMode(52, 67);   // 干预控制锁定阶段 (shares device code with manual)
        mapMode(32, 67);   // 全息干线自适应
        mapMode(24, 67);   // 单点全息自适应
        mapMode(34, 67);   // 中心干线协调控制
        mapMode(26, 67);   // 中心定周期
        mapMode(21, 33);   // 单点多时段定时控制
        mapMode(13, 49);   // 黄闪控制
        mapMode(12, 50);   // 全红控制
        mapMode(11, 51);   // 关灯
        mapMode(35, 35);   // 本地协调控制
        mapMode(61, 67);   // 信号机手动控制
    }

    private static void mapMode(int standard, int device) {
        STANDARD_TO_DEVICE.put(standard, device);
        DEVICE_TO_STANDARD.putIfAbsent(device, standard);
    }

    @Override
    public String vendorId() {
        return "keli";
    }

    @Override
    public String vendorName() {
        return "科力";
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
        return ConnectionStrategy.TCP;
    }
}
