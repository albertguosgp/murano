package com.murano.oms.base.service;

import java.nio.file.Path;
import java.util.List;

public interface MailService {
    void send(List<String> recipient, List<String> cc, String subject, String body, List<Path> attachments);
}
