package com.farmer.service;

import com.farmer.entity.Order;
import com.farmer.entity.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

@Service
@EnableAsync
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendWelcomeEmail(User user) {
        logger.info(">>> EMAIL TASK: Welcome email for {}", user.getEmail());
        try {
            Context context = new Context();
            context.setVariable("name", user.getName());
            String htmlContent = templateEngine.process("emails/welcome", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Welcome to ROOTS - Your Agri Trading Partner");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Welcome email SENT to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("❌ Failed to send welcome email to: {}. Error: {}", user.getEmail(), e.getMessage());
            // FALLBACK: Log the simplified content
            System.out.println("\n--- [EMAIL FALLBACK - WELCOME] ---");
            System.out.println("To: " + user.getEmail());
            System.out.println("Subject: Welcome to ROOTS");
            System.out.println("Body: Hello " + user.getName() + ", welcome to the platform!");
            System.out.println("----------------------------------\n");
        }
    }

    @Async
    public void sendOrderPlacedEmail(Order order) {
        String recipientEmail = order.getRetailer().getEmail();
        logger.info(">>> EMAIL TASK: Order Confirmation #{} for {}", order.getId(), recipientEmail);
        try {
            Context context = new Context();
            context.setVariable("order", order);
            String htmlContent = templateEngine.process("emails/order-placed", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Order Confirmed - #" + order.getId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Order confirmation email SENT for Order #{} to: {}", order.getId(), recipientEmail);
        } catch (Exception e) {
            logger.error("❌ Failed to send order confirmation for Order #{}. Error: {}", order.getId(), e.getMessage());
            // FALLBACK
            System.out.println("\n--- [EMAIL FALLBACK - ORDER PLACED] ---");
            System.out.println("To: " + recipientEmail);
            System.out.println("Subject: Order Confirmed #" + order.getId());
            System.out.println("Items count: " + (order.getItems() != null ? order.getItems().size() : 0));
            System.out.println("Total: " + order.getTotalAmount());
            System.out.println("--------------------------------------\n");
        }
    }

    @Async
    public void sendPaymentSuccessEmail(Order order, File invoiceFile) {
        String recipientEmail = order.getRetailer().getEmail();
        logger.info("Preparing to send payment success email for Order #{} to: {}", order.getId(), recipientEmail);
        try {
            Context context = new Context();
            context.setVariable("order", order);
            String htmlContent = templateEngine.process("emails/payment-success", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Payment Received - Invoice for Order #" + order.getId());
            helper.setText(htmlContent, true);

            if (invoiceFile != null && invoiceFile.exists()) {
                helper.addAttachment("Invoice_" + order.getId() + ".pdf", invoiceFile);
            }

            mailSender.send(message);
            logger.info("Payment success email sent successfully for Order #{} to: {}", order.getId(), recipientEmail);
        } catch (Exception e) {
            logger.error("Failed to send payment success email for Order #{}. Error: {}", order.getId(),
                    e.getMessage());
            e.printStackTrace();
        }
    }

    @Async
    public void sendComplaintResolvedEmail(com.farmer.entity.Complaint complaint) {
        String recipientEmail = complaint.getRetailer().getEmail();
        logger.info("Preparing to send complaint resolution email for Complaint #{} to: {}", complaint.getId(),
                recipientEmail);
        try {
            Context context = new Context();
            context.setVariable("complaint", complaint);
            String htmlContent = templateEngine.process("emails/complaint-resolved", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Complaint Resolved - #" + complaint.getId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Complaint resolution email sent successfully for Complaint #{} to: {}", complaint.getId(),
                    recipientEmail);
        } catch (Exception e) {
            logger.error("Failed to send complaint resolution for Complaint #{}. Error: {}", complaint.getId(),
                    e.getMessage());
            e.printStackTrace();
        }
    }

    @Async
    public void sendVerificationStatusEmail(com.farmer.entity.FarmerDocument doc) {
        String recipientEmail = doc.getFarmer().getEmail();
        logger.info("Preparing to send verification update email to: {}", recipientEmail);
        try {
            Context context = new Context();
            context.setVariable("doc", doc);
            String htmlContent = templateEngine.process("emails/verification-update", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Profile Verification Status: " + doc.getStatus());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Verification update email sent successfully to: {}", recipientEmail);
        } catch (Exception e) {
            logger.error("Failed to send verification update to: {}. Error: {}", recipientEmail, e.getMessage());
            e.printStackTrace();
        }
    }

    // @Async - Commented out for debugging
    public void sendOtpEmail(String to, String otp) {
        logger.info("Sending OTP {} to {}", otp, to);

        // ALWAYS WRITE OTP TO FILE (For testing/debugging)
        try (java.io.FileWriter fw = new java.io.FileWriter("OTP.txt")) {
            fw.write("OTP for " + to + ": " + otp);
            // Print location to console so user can find it
            System.out.println(">> OTP FILE WRITTEN TO: " + new java.io.File("OTP.txt").getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("ROOTS Registration OTP");
            message.setText("Your One-Time Password (OTP) for registration is: " + otp
                    + "\n\nThis code is valid for 5 minutes.");

            mailSender.send(message);
            logger.info("OTP email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("   BUT HERE IS YOUR OTP FOR TESTING:");
            logger.error("   OTP: [{}]", otp);
            logger.error("==================================================");

            // WRITE TO FILE FOR EASY ACCESS
            try (java.io.FileWriter fw = new java.io.FileWriter("OTP.txt")) {
                fw.write("YOUR LATEST OTP IS: " + otp);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
