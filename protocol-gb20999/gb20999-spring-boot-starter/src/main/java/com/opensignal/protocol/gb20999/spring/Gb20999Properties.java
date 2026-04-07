package com.opensignal.protocol.gb20999.spring;

import com.opensignal.protocol.common.spi.ConnectionStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

/**
 * Configuration properties for GB/T 20999 client and optional server.
 */
@ConfigurationProperties(prefix = "gb20999")
@Data
public class Gb20999Properties {

    private boolean enabled = true;

    private ClientProperties client = new ClientProperties();

    private ServerProperties server = new ServerProperties();

    /**
     * Resolves {@link ClientProperties#transport} to a {@link ConnectionStrategy}.
     */
    public ConnectionStrategy resolveClientTransport() {
        String raw = client.getTransport();
        if (raw == null || raw.trim().isEmpty()) {
            return ConnectionStrategy.TCP;
        }
        try {
            return ConnectionStrategy.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Invalid gb20999.client.transport: " + raw
                    + " (expected TCP, UDP, or SERIAL)", ex);
        }
    }

    @Data
    public static class ClientProperties {

        private String host;

        private int port = 9000;

        private String vendorId;

        private int upperId = 1;

        private int signalId = 1;

        private int crossId = 1;

        private long connectTimeoutMs = 5000L;

        private long responseTimeoutMs = 10000L;

        private long heartbeatIntervalMs = 30000L;

        private boolean autoReconnect = true;

        private int maxReconnectAttempts = 3;

        private String transport = "TCP";
    }

    @Data
    public static class ServerProperties {

        private boolean enabled = false;

        private String bindHost = "0.0.0.0";

        private int bindPort = 9000;

        private int signalId = 1;

        private int crossId = 1;

        private int upperId = 1;
    }
}
