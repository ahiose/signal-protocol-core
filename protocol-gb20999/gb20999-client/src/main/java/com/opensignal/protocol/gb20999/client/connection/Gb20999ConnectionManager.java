package com.opensignal.protocol.gb20999.client.connection;

import com.opensignal.protocol.common.exception.ConnectionException;
import com.opensignal.protocol.common.spi.ConnectionStrategy;
import com.opensignal.protocol.common.transport.Connection;
import com.opensignal.protocol.common.transport.ConnectionConfig;
import com.opensignal.protocol.common.transport.ConnectionManager;
import com.opensignal.protocol.gb20999.client.Gb20999Client;
import com.opensignal.protocol.gb20999.client.Gb20999ClientConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages multiple {@link Gb20999Client} instances as {@link Connection}s.
 */
public class Gb20999ConnectionManager implements ConnectionManager {

    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    @Override
    public Connection connect(ConnectionConfig config) {
        if (config == null || config.getRemoteAddress() == null) {
            throw new IllegalArgumentException("ConnectionConfig and remoteAddress are required");
        }
        if (config.getStrategy() == ConnectionStrategy.SERIAL) {
            throw new ConnectionException("SERIAL strategy is not supported");
        }

        String connectionId = buildConnectionId(config);
        Gb20999ClientConfig clientConfig = Gb20999ClientConfig.builder()
                .remoteAddress(config.getRemoteAddress())
                .vendorId(config.getVendorId())
                .transport(config.getStrategy())
                .connectTimeoutMs(config.getConnectTimeoutMs())
                .responseTimeoutMs(config.getResponseTimeoutMs())
                .heartbeatIntervalMs(config.getHeartbeatIntervalMs())
                .autoReconnect(config.isAutoReconnect())
                .maxReconnectAttempts(config.getMaxReconnectAttempts())
                .build();

        Gb20999Client client = Gb20999Client.create(clientConfig);
        client.connect();
        Gb20999Connection connection = new Gb20999Connection(connectionId, client);
        connections.put(connectionId, connection);
        return connection;
    }

    private String buildConnectionId(ConnectionConfig config) {
        return config.getRemoteAddress().getHostString()
                + ":" + config.getRemoteAddress().getPort()
                + "-" + config.getStrategy().name()
                + "-" + idCounter.incrementAndGet();
    }

    @Override
    public Optional<Connection> getConnection(String connectionId) {
        return Optional.ofNullable(connections.get(connectionId));
    }

    @Override
    public Collection<Connection> allConnections() {
        return Collections.unmodifiableCollection(new ArrayList<>(connections.values()));
    }

    @Override
    public void disconnect(String connectionId) {
        Connection c = connections.remove(connectionId);
        if (c != null) {
            c.close();
        }
    }

    @Override
    public void close() {
        for (String id : new ArrayList<>(connections.keySet())) {
            disconnect(id);
        }
    }
}
