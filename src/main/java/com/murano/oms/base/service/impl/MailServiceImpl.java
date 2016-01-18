package com.murano.oms.base.service.impl;

import com.google.common.annotations.VisibleForTesting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.murano.oms.base.service.MailService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Setter
    @Value("${spring.mail.attachment.threshold: 5000000}")
    private long mailAttachmentSizeThreshold;

    @Value("${spring.mail.host: localhost}")
    private String smtpHost;

    @Value("${spring.mail.port: 25}")
    private int smtpPort;

    @Value("${spring.mail.default-encoding: UTF-8}")
    private String defaultEmailEncoding;

    @Setter
    @Value("${application.support.email: support@flextrade.com}")
    private String applicationSupportEmail;

    @Setter
    @Autowired
    private JavaMailSender mailSender;

    @PostConstruct
    public void printMailConfiguration() {
        log.info("SMTP server host is {} port {} ", smtpHost, smtpPort);
        log.info("Mail is encoded with {} attachment size threshold {}", defaultEmailEncoding, mailAttachmentSizeThreshold);
    }

    @Override
    public void send(List<String> recipients, List<String> cc, String subject, String body, List<Path> attachments) {
        requireNonNull(recipients, "Email recipients should not be null");
        requireNonNull(subject, "Email message must contain subject");
        requireNonNull(body, "Email message must contain body");
        log.info("Sending email to {} cc {} subject {} body {} with attachments {}", recipients, cc, subject, body, attachments);
        MimeMessage mailMessage = createMailMessage(recipients, cc, subject, body, attachments);

        try {
            mailSender.send(mailMessage);
            log.info("Email sent successfully");
        } catch (MailException e) {
            log.error("Failed to send email message", e);
        }
    }

    @VisibleForTesting
    MimeMessage createMailMessage(List<String> recipients, List<String> cc, String subject, String body, List<Path> attachments) {
        MimeMessage message = null;
        try {
            message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom(applicationSupportEmail);
            mimeMessageHelper.setTo(recipients.toArray(new String[recipients.size()]));
            if (!CollectionUtils.isEmpty(cc)) {
                mimeMessageHelper.setCc(cc.toArray(new String[cc.size()]));
            }
            mimeMessageHelper.setSubject(subject);

            String attachmentPath = createAttachmentPath(attachments);
            mimeMessageHelper.setText(body + attachmentPath);

            attachAttachments(mimeMessageHelper, attachments);

        } catch (Exception e) {
            log.error("Failed to create mail message ", e);
            throw new RuntimeException(e);
        }
        return message;
    }

    private void attachAttachments(MimeMessageHelper mimeMessageHelper, List<Path> attachments) {
        long aggregatedAttachmentSize = aggregateAttachmentSize(attachments);
        if (aggregatedAttachmentSize <= mailAttachmentSizeThreshold) {
            attachments.forEach(path -> {
                try {
                    mimeMessageHelper.addAttachment(path.getFileName().toString(), path.toFile());
                } catch (MessagingException e) {
                    log.error("Failed to attach attachments in email message", e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private String createAttachmentPath(List<Path> attachments) {
        long aggregatedAttachmentSize = aggregateAttachmentSize(attachments);
        StringBuilder attachmentPath = new StringBuilder();
        if (aggregatedAttachmentSize > mailAttachmentSizeThreshold) {
            attachmentPath.append(lineSeparator())
                    .append("Attachment size exceeds ")
                    .append(mailAttachmentSizeThreshold)
                    .append(lineSeparator())
                    .append("Please ask help from ")
                    .append(applicationSupportEmail)
                    .append(lineSeparator())
                    .append("Attachment in server location is ");
            attachments.stream().forEach(path -> {
                attachmentPath.append(path.toAbsolutePath().toString());
                attachmentPath.append(lineSeparator());
            });
        }

        return attachmentPath.toString();
    }

    private long aggregateAttachmentSize(List<Path> attachments) {
        return attachments.stream().mapToLong(path -> {
            try {
                return Files.size(path);
            } catch (IOException e) {
                log.error("Failed to retrieve path {} size ", path);
                throw new RuntimeException(e);
            }
        }).sum();
    }

}
