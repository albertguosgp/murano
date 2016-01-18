package com.murano.oms.report.task;

import com.murano.oms.base.service.MailService;
import com.murano.oms.base.service.impl.FreezableTimeServiceImpl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import flextrade.flexvision.fx.audit.json.AuditLogQuery;
import flextrade.flexvision.fx.audit.pojo.AuditLog;
import flextrade.flexvision.fx.audit.service.AuditLogService;

import static flextrade.flexvision.fx.audit.pojo.AuditLog.of;
import static flextrade.flexvision.fx.report.task.AuditLogReportTask.FILE_HEADER_MAPPING;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuditLogReportTaskTest {
    @Mock
    AuditLogService auditLogService;

    @Mock
    AuditLogQuery auditLogQuery;

    @Mock
    MailService mailService;

    FreezableTimeServiceImpl timeService = new FreezableTimeServiceImpl();

    private List<AuditLog> auditLogs;

    private Path tempCsvFile;

    @Before
    public void before() {
        timeService.setPreferredTimezone("America/New_York");

        ZonedDateTime nonFarmPayrollDay = ZonedDateTime.of(2015, 8, 23, 12, 11, 10, 0, ZoneId.of("America/New_York"));
        AuditLog johnDoeAuditLog = of(123l, "Johndoe", "Remove user Mike", Date.from(nonFarmPayrollDay.toInstant()), "User Mike is no longer used");
        AuditLog johnRoeAuditLog = of(124l, "JohnRoe", "Remove user Jenny", Date.from(nonFarmPayrollDay.toInstant()), "User Jenny is no longer used");
        auditLogs = Arrays.asList(johnDoeAuditLog, johnRoeAuditLog);

        when(auditLogService.get(auditLogQuery)).thenReturn(auditLogs);
    }

    @After
    public void cleanUp() throws IOException {
        if (tempCsvFile != null) {
            Files.deleteIfExists(tempCsvFile);
        }
    }

    @Test
    public void should_create_temp_audit_log_csv() throws Exception {
        AuditLogReportTask auditLogReportTask = new AuditLogReportTask(auditLogQuery, auditLogService, mailService, timeService);
        tempCsvFile = auditLogReportTask.call();
        List<AuditLog> actualAuditFromCsv = readAuditLogsFromCsv(tempCsvFile);

        assertThat(auditLogs, equalTo(actualAuditFromCsv));
    }

    private List<AuditLog> readAuditLogsFromCsv(Path tempCsvFile) throws IOException {
        CSVFormat auditLogCsvFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        FileReader fileReader = new FileReader(tempCsvFile.toFile());
        CSVParser csvFileParser = new CSVParser(fileReader, auditLogCsvFormat);

        List<AuditLog> auditLogsFromCsv = convertToAuditLogs(csvFileParser);
        fileReader.close();

        return auditLogsFromCsv;
    }

    private List<AuditLog> convertToAuditLogs(CSVParser csvFileParser) throws IOException {
        return csvFileParser.getRecords().stream().skip(1).map(csvRecord -> {
            long id = Long.valueOf(csvRecord.get(FILE_HEADER_MAPPING[0]));
            String maxxUser = csvRecord.get(FILE_HEADER_MAPPING[1]);
            String operation = csvRecord.get(FILE_HEADER_MAPPING[2]);
            ZonedDateTime auditDate = ZonedDateTime.parse(csvRecord.get(FILE_HEADER_MAPPING[3]));
            String remarks = csvRecord.get(FILE_HEADER_MAPPING[4]);

            return of(id, maxxUser, operation, Date.from(auditDate.toInstant()), remarks);
        }).collect(Collectors.toList());
    }
}