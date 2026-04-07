package com.opensignal.protocol.gb20999.server;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration for {@link Gb20999Server}.
 */
@Data
@Builder
public class Gb20999ServerConfig {

    /** Matches model {@code RunStatus} (运行状态, class id 13 in project model javadoc). */
    public static final int RUN_STATUS_DATA_CLASS_ID = 13;

    @Builder.Default
    private int bindPort = 9000;

    @Builder.Default
    private String bindHost = "0.0.0.0";

    @Builder.Default
    private int signalId = 1;

    @Builder.Default
    private int crossId = 1;

    @Builder.Default
    private int upperId = 1;
}
