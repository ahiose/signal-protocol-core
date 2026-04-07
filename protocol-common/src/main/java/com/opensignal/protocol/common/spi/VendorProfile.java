package com.opensignal.protocol.common.spi;

import java.util.Collections;
import java.util.List;

/**
 * Extension point for signal controller vendor-specific behavior.
 * Loaded via {@link java.util.ServiceLoader}.
 *
 * <p>Implement this interface to adapt the SDK to a specific vendor's
 * signal controller. The SDK falls back to standard behavior for any
 * method returning default values.
 */
public interface VendorProfile {

    String vendorId();

    String vendorName();

    int toDeviceControlMode(int standardMode);

    int toStandardControlMode(int deviceMode);

    default DataValueCodec customCodec(int dataClassId, int attributeId) {
        return null;
    }

    default List<DataClassDefinition> extendedDataClasses() {
        return Collections.emptyList();
    }

    default CommandInterceptor commandInterceptor() {
        return CommandInterceptor.NOOP;
    }

    default ConnectionStrategy connectionStrategy() {
        return ConnectionStrategy.TCP;
    }
}
