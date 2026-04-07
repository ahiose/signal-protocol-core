package com.opensignal.protocol.gb20999.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class 18 — 命令管道 (CommandPipe).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class    CommandPipe {

    /** Object 1 — 命令值 */
    private int commandValue;
}
