package com.murano.oms.base.service;

import java.time.ZonedDateTime;

/**
 * Freezable time service is created for testing purpose. With freezable time service, we can use
 * JMX lock the time and do all necessary tests.
 */
public interface FreezableTimeService extends TimeService {
    void freezeTimeTo(ZonedDateTime timeFrozeTo);

    void unfreezeTime();
}
