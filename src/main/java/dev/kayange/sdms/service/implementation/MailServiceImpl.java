package dev.kayange.sdms.service.implementation;

import dev.kayange.sdms.enumeration.EmailTemplate;
import dev.kayange.sdms.exception.ApiException;
import dev.kayange.sdms.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${application.mail.email-address}")
    private String emailAddress;

    @Override
    @Async
    public void sendMail(String to, String username, EmailTemplate emailTemplate, String confirmationUrl, String activationCode, String subject) {
        String templateName;
        if(emailTemplate == null) {
            templateName = "activate_account";
        }else {
            templateName = emailTemplate.getTemplateName();
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());
            Map<String, Object> properties = Map.of("username",username, "confirmationUrl", confirmationUrl, "activationCode", activationCode);
            Context context = new Context();
            context.setVariables(properties);

            var template = templateEngine.process(templateName, context);
            helper.setFrom(emailAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(template, true);
            mailSender.send(mimeMessage);
        }catch (MessagingException e){
            log.error(e.getMessage());
            throw new ApiException("Failed to Send Email");
        }catch (Exception e){
            throw new ApiException("Something went wrong");
        }
    }
}
