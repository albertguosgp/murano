package com.murano.oms.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;

@Data
public class Tick {
    private Symbol symbol;
    private Instant sendTime;
    private FxValueDate valueDate;
    private BigDecimal price;
}
