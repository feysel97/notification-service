package com.microservice.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        // 1. Prepare the Thymeleaf context with our variables
        Context context = new Context();
        context.setVariable("subject", subject);
        context.setVariable("message", body);

        // 2. Process the HTML template
        String htmlContent = templateEngine.process("email-template", context);

        // 3. Create and send the MimeMessage (HTML email)
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // The 'true' flag indicates HTML

        mailSender.send(mimeMessage);
    }
}
