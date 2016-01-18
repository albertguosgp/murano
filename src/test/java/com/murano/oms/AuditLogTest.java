package com.murano.oms;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import flextrade.flexvision.fx.audit.pojo.AuditLog;

import static org.junit.Assert.assertEquals;

public class AuditLogTest {

    @Test
    public void should_implement_proper_equals_hashcode_method() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        AuditLog johnDoeAuditLog = new AuditLog();
        johnDoeAuditLog.setId(1l);
        johnDoeAuditLog.setMaxxUser("Johndoe");
        johnDoeAuditLog.setOperation("Disable HK sales firm");
        johnDoeAuditLog.setRemarks("HK sales firm is no longer required");
        johnDoeAuditLog.setAuditDate(dateFormatter.parse("2015-09-01"));

        AuditLog johnDoeAuditLogCopy = new AuditLog();
        johnDoeAuditLogCopy.setId(1l);
        johnDoeAuditLogCopy.setMaxxUser("Johndoe");
        johnDoeAuditLogCopy.setOperation("Disable HK sales firm");
        johnDoeAuditLogCopy.setRemarks("HK sales firm is no longer required");
        johnDoeAuditLogCopy.setAuditDate(dateFormatter.parse("2015-09-01"));

        assertEquals(johnDoeAuditLog, johnDoeAuditLogCopy);
    }
}
