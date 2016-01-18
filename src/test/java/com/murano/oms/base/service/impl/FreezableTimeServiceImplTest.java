package com.murano.oms.base.service.impl;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FreezableTimeServiceImplTest {

    private static final FreezableTimeServiceImpl freezableTimeService = new FreezableTimeServiceImpl();

    @Test
    public void should_be_able_to_freeze_time() throws InterruptedException {
        freezeTime();
        Thread.sleep(300);

        assertThat(freezableTimeService.now(), Matchers.is(getTimeFrozeTo()));
    }

    @Test
    public void should_be_able_to_unfreeze_time() throws InterruptedException {
        freezeTime();
        Thread.sleep(300);
        freezableTimeService.unfreezeTime();

        assertTrue(freezableTimeService.now().isAfter(getTimeFrozeTo()));
    }

    @Test
    public void should_be_able_to_display_in_preferred_timezone() {
        String preferredTimezone = "Asia/Tokyo";
        String expectedTimeInTokyo = "2015-11-01T11:12:11+09:00[Asia/Tokyo]";
        freezableTimeService.setPreferredTimezone(preferredTimezone);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2015, 11, 1, 11, 12, 11, 0, ZoneId.of(preferredTimezone));

        assertEquals(expectedTimeInTokyo, freezableTimeService.displayInPreferredTimezone(Date.from(zonedDateTime.toInstant())));
    }

    private void freezeTime() {
        ZonedDateTime timeFrozeTo = getTimeFrozeTo();
        freezableTimeService.freezeTimeTo(timeFrozeTo);
    }

    private ZonedDateTime getTimeFrozeTo() {
        LocalDate sgp13Sep2015 = LocalDate.of(2015, 9, 13);
        LocalTime sgp23Hours15mins3Secs = LocalTime.of(23, 15, 3);
        ZoneId sgpTimeZone = getSGPTimeZone();
        return ZonedDateTime.of(sgp13Sep2015, sgp23Hours15mins3Secs, sgpTimeZone);
    }

    private ZoneId getSGPTimeZone() {
        return ZoneId.of("Asia/Singapore");
    }
}