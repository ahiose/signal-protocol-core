package com.opensignal.protocol.common.transport;

import com.opensignal.protocol.common.spi.VendorProfile;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a connection to a signal controller, bound to a specific
 * {@link VendorProfile} for the lifetime of the connection.
 */
public interface Connection extends AutoCloseable {

    String id();

    InetSocketAddress remoteAddress();

    VendorProfile vendorProfile();

    boolean isActive();

    CompletableFuture<byte[]> send(byte[] data);

    CompletableFuture<byte[]> send(byte[] data, long timeoutMs);

    @Override
    void close();
}
