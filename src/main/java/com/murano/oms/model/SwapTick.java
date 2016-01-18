package com.murano.oms.model;

import lombok.Data;

@Data
public class SwapTick {
    private Tick nearLegTick;
    private Tick farLegTick;
}
