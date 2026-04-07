package com.opensignal.protocol.common.spi;

/**
 * Default profile with no vendor-specific behavior.
 * Control mode values pass through unchanged.
 */
public final class StandardProfile implements VendorProfile {

    public static final StandardProfile INSTANCE = new StandardProfile();

    private StandardProfile() {}

    @Override
    public String vendorId() {
        return "standard";
    }

    @Override
    public String vendorName() {
        return "GB/T 20999 Standard";
    }

    @Override
    public int toDeviceControlMode(int standardMode) {
        return standardMode;
    }

    @Override
    public int toStandardControlMode(int deviceMode) {
        return deviceMode;
    }
}
