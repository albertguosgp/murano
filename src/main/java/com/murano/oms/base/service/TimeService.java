package com.murano.oms.base.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public interface TimeService {
    ZonedDateTime now();

    LocalDate valueDate();

    String displayInPreferredTimezone(Date date);

    static String toISO8601Format(Date date) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return zonedDateTime.toInstant().toString();
    }

    static Date toDate(String iso8601FormatDate) throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");
        return dateFormatter.parse(iso8601FormatDate);
    }
}
