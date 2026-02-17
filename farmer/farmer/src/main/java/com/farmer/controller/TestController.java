package com.farmer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/test-email")
    public String sendTestEmail(@RequestParam String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Test Email from ROOTS");
            message.setText("If you are reading this, your SMTP configuration is correct!");

            mailSender.send(message);
            return "Email sent successfully to " + email;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending email: " + e.getMessage();
        }
    }
}
