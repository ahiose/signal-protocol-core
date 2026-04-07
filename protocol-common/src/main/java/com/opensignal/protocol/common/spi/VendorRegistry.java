package com.opensignal.protocol.common.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Discovers and manages {@link VendorProfile} instances via SPI.
 *
 * <p>Profiles are loaded lazily via {@link ServiceLoader} on first access,
 * and can also be registered programmatically.
 */
public final class VendorRegistry {

    private static final VendorRegistry INSTANCE = new VendorRegistry();

    private final Map<String, VendorProfile> profiles = new ConcurrentHashMap<>();
    private volatile boolean loaded = false;

    private VendorRegistry() {}

    public static VendorRegistry getInstance() {
        return INSTANCE;
    }

    public void register(VendorProfile profile) {
        profiles.put(profile.vendorId(), profile);
    }

    public VendorProfile resolve(String vendorId) {
        ensureLoaded();
        if (vendorId == null) {
            return StandardProfile.INSTANCE;
        }
        return profiles.getOrDefault(vendorId, StandardProfile.INSTANCE);
    }

    public Map<String, VendorProfile> all() {
        ensureLoaded();
        return Collections.unmodifiableMap(new HashMap<>(profiles));
    }

    private void ensureLoaded() {
        if (!loaded) {
            synchronized (this) {
                if (!loaded) {
                    ServiceLoader.load(VendorProfile.class)
                            .forEach(p -> profiles.putIfAbsent(p.vendorId(), p));
                    loaded = true;
                }
            }
        }
    }
}
