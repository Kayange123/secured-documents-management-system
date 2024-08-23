package dev.kayange.sdms.service;

import dev.kayange.sdms.enumeration.EmailTemplate;

public interface MailService {
    void sendMail(String to, String username, EmailTemplate emailTemplate, String confirmationUrl,
                  String activationCode, String subject);
}
