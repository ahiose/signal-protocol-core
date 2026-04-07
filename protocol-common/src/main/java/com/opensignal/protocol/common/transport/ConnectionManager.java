package com.opensignal.protocol.common.transport;

import java.util.Collection;
import java.util.Optional;

/**
 * Manages multiple connections, each potentially bound to a different vendor.
 */
public interface ConnectionManager extends AutoCloseable {

    Connection connect(ConnectionConfig config);

    Optional<Connection> getConnection(String connectionId);

    Collection<Connection> allConnections();

    void disconnect(String connectionId);

    @Override
    void close();
}
