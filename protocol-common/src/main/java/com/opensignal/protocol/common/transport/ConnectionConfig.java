package com.opensignal.protocol.common.transport;

import com.opensignal.protocol.common.spi.ConnectionStrategy;
import lombok.Builder;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
@Builder
public class ConnectionConfig {

    private final InetSocketAddress remoteAddress;

    @Builder.Default
    private final ConnectionStrategy strategy = ConnectionStrategy.TCP;

    @Builder.Default
    private final long connectTimeoutMs = 5000;

    @Builder.Default
    private final long responseTimeoutMs = 10000;

    @Builder.Default
    private final long heartbeatIntervalMs = 30000;

    @Builder.Default
    private final boolean autoReconnect = true;

    @Builder.Default
    private final int maxReconnectAttempts = 3;

    private final String vendorId;
}
