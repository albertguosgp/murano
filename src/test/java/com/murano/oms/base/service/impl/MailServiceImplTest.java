package com.murano.oms.base.service.impl;

import com.murano.oms.base.util.MimeMessageParser;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.mail.internet.MimeMessage;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MailServiceImplTest {

    private MailServiceImpl mailService;

    private long mailAttachmentSizeThreshold = 9;

    @Before
    public void setup() {
        JavaMailSender mailSender = new JavaMailSenderImpl();
        mailService = new MailServiceImpl();

        mailService.setMailSender(mailSender);
        mailService.setApplicationSupportEmail("support@flextrade.com");
        mailService.setMailAttachmentSizeThreshold(mailAttachmentSizeThreshold);
    }

    @Test
    public void should_not_send_email_with_attachment_if_attachment_size_exceed_mail_attachment_size_threshold() throws Exception {
        List<String> recipients = createRecipients();
        List<String> cc = createCc();
        List<Path> attachmentExceedsFileSizeThreshold = createAttachmentExceedsFileSizeThreshold(true);
        MimeMessageParser mailMessageParser = parseMimeMessage(recipients, cc, attachmentExceedsFileSizeThreshold);

        List<String> actualRecipients = mailMessageParser.getTo().stream().map(Object::toString).collect(toList());

        List<String> actualCc = mailMessageParser.getCc().stream().map(Object::toString).collect(toList());
        String actualBody = mailMessageParser.getPlainContent();

        assertThat(actualCc, equalTo(cc));
        assertThat(actualRecipients, equalTo(recipients));
        assertThat(actualBody, containsString("exceeds " + mailAttachmentSizeThreshold));
        assertFalse(mailMessageParser.hasAttachments());

    }

    @Test
    public void should_send_email_with_attachment_if_attachment_size_not_exceed_mail_attachment_size_threshold() throws Exception {
        List<String> recipients = createRecipients();
        List<String> cc = createCc();
        List<Path> attachmentExceedsFileSizeThreshold = createAttachmentExceedsFileSizeThreshold(false);
        MimeMessageParser mailMessageParser = parseMimeMessage(recipients, cc, attachmentExceedsFileSizeThreshold);

        assertTrue(mailMessageParser.hasAttachments());
        assertEquals(mailMessageParser.getAttachmentList().size(), 1);
    }

    private MimeMessageParser parseMimeMessage(List<String> recipients, List<String> cc, List<Path> attachmentExceedsFileSizeThreshold) throws Exception {
        MimeMessage mailMessage = mailService.createMailMessage(recipients, cc, "Email subject", "Email body", attachmentExceedsFileSizeThreshold);
        mailMessage.saveChanges();

        MimeMessageParser mailMessageParser = new MimeMessageParser(mailMessage);
        mailMessageParser.parse();
        return mailMessageParser;
    }

    private List<String> createCc() {
        return asList("ccEmail1@flextrade.com", "ccEmail2@flextrade.com", "ccEmail3@flextrade.com");
    }

    private List<String> createRecipients() {
        return asList("email1@flextrade.com", "email2@flextrade.com", "email3@flextrade.com");
    }

    private List<Path> createAttachmentExceedsFileSizeThreshold(boolean shouldExceedThreshold) throws IOException {
        File tempFile = File.createTempFile("test-", ".tmp");
        BufferedWriter bufferedWriter = Files.newBufferedWriter(tempFile.toPath(), StandardCharsets.UTF_8);
        if (shouldExceedThreshold) {
            bufferedWriter.write("1234567890");
        } else {
            bufferedWriter.write("1");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        tempFile.deleteOnExit();

        return Arrays.asList(tempFile.toPath());
    }

}