package com.murano.oms.base.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.murano.oms.base.service.FreezableTimeService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FreezableTimeServiceImpl implements FreezableTimeService {

    @Getter
    @Setter
    @Value("${preference.timezone: America/New_York}")
    private String preferredTimezone;

    private ZonedDateTime now;
    private Lock lock = new ReentrantLock();

    @Override
    public void freezeTimeTo(ZonedDateTime timeFrozeTo) {
        try {
            lock.lock();
            log.debug("Freezing time to {}", timeFrozeTo);
            now = timeFrozeTo;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unfreezeTime() {
        try {
            lock.lock();
            log.debug("Unfreezing time");
            now = null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ZonedDateTime now() {
        return now == null ? ZonedDateTime.now() : now;
    }

    @Override
    public LocalDate valueDate() {
        return now().withZoneSameInstant(ZoneId.of("America/New_York")).toLocalDate();
    }

    @Override
    public String displayInPreferredTimezone(Date date) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of(preferredTimezone));
        return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(dateTime);
    }
}
