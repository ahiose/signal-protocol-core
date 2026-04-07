package com.opensignal.protocol.gb20999.client.handler;

import com.opensignal.protocol.gb20999.client.Gb20999Client;

/**
 * Periodic heartbeat task (not a Netty pipeline handler).
 */
public class HeartbeatHandler implements Runnable {

    private final Gb20999Client client;

    public HeartbeatHandler(Gb20999Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        client.runHeartbeatTick();
    }
}
