package com.opensignal.protocol.gb20999.spring;

import com.opensignal.protocol.gb20999.client.Gb20999Client;
import com.opensignal.protocol.gb20999.client.Gb20999ClientConfig;
import com.opensignal.protocol.gb20999.server.Gb20999Server;
import com.opensignal.protocol.gb20999.server.Gb20999ServerConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@EnableConfigurationProperties(Gb20999Properties.class)
@ConditionalOnClass(Gb20999Client.class)
@ConditionalOnProperty(prefix = "gb20999", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Gb20999AutoConfiguration {

    @Bean(initMethod = "start", destroyMethod = "close")
    @ConditionalOnClass(Gb20999Server.class)
    @ConditionalOnProperty(prefix = "gb20999.server", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(Gb20999Server.class)
    public Gb20999Server gb20999Server(Gb20999Properties properties) {
        Gb20999Properties.ServerProperties s = properties.getServer();
        return Gb20999Server.create(Gb20999ServerConfig.builder()
                .bindHost(s.getBindHost())
                .bindPort(s.getBindPort())
                .signalId(s.getSignalId())
                .crossId(s.getCrossId())
                .upperId(s.getUpperId())
                .build());
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(prefix = "gb20999.client", name = "host")
    @ConditionalOnMissingBean(Gb20999Client.class)
    public Gb20999Client gb20999Client(Gb20999Properties properties,
            ObjectProvider<Gb20999Server> embeddedServer) {
        embeddedServer.ifAvailable(s -> { });
        Gb20999Properties.ClientProperties c = properties.getClient();
        Gb20999ClientConfig.Gb20999ClientConfigBuilder builder = Gb20999ClientConfig.builder()
                .remoteAddress(new InetSocketAddress(c.getHost(), c.getPort()))
                .upperId(c.getUpperId())
                .signalId(c.getSignalId())
                .crossId(c.getCrossId())
                .connectTimeoutMs(c.getConnectTimeoutMs())
                .responseTimeoutMs(c.getResponseTimeoutMs())
                .heartbeatIntervalMs(c.getHeartbeatIntervalMs())
                .autoReconnect(c.isAutoReconnect())
                .maxReconnectAttempts(c.getMaxReconnectAttempts())
                .transport(properties.resolveClientTransport());
        if (c.getVendorId() != null && !c.getVendorId().isEmpty()) {
            builder.vendorId(c.getVendorId());
        }
        Gb20999Client client = Gb20999Client.create(builder.build());
        client.connect();
        return client;
    }
}
