package com.opensignal.protocol.gb20999.client;

import com.opensignal.protocol.common.spi.ConnectionStrategy;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.net.InetSocketAddress;

/**
 * Configuration for {@link Gb20999Client}.
 */
@Data
@Builder
public class Gb20999ClientConfig {

    @NonNull
    private InetSocketAddress remoteAddress;

    private String vendorId;

    @Builder.Default
    private int upperId = 1;

    @Builder.Default
    private int signalId = 1;

    @Builder.Default
    private int crossId = 1;

    @Builder.Default
    private long connectTimeoutMs = 5000L;

    @Builder.Default
    private long responseTimeoutMs = 10000L;

    @Builder.Default
    private long heartbeatIntervalMs = 30000L;

    @Builder.Default
    private boolean autoReconnect = true;

    @Builder.Default
    private int maxReconnectAttempts = 3;

    @Builder.Default
    private ConnectionStrategy transport = ConnectionStrategy.TCP;
}
