package Project_ITSS.common.service.impl;

import Project_ITSS.common.service.IEmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("commonEmailNotificationService")
public class EmailNotificationServiceImpl implements IEmailNotificationService {

    @Autowired
    public EmailNotificationServiceImpl() {
        // Constructor for dependency injection
    }

    @Override
    public void sendSuccessEmail(String email, String subject, String message) {
        // Implementation for sending success email
        // This would typically integrate with email service providers
        System.out.println("Sending success email to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
    }

    @Override
    public void sendErrorEmail(String email, String subject, String message) {
        // Implementation for sending error email
        System.out.println("Sending error email to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
    }

    @Override
    public void sendOrderConfirmationEmail(String email, long orderId) {
        String subject = "Order Confirmation";
        String message = "Your order #" + orderId + " has been confirmed.";
        sendSuccessEmail(email, subject, message);
    }

    @Override
    public void sendOrderCancellationEmail(String email, long orderId) {
        String subject = "Order Cancellation";
        String message = "Your order #" + orderId + " has been cancelled.";
        sendSuccessEmail(email, subject, message);
    }

    @Override
    public void sendOrderStatusUpdateEmail(String email, long orderId, String status) {
        String subject = "Order Status Update";
        String message = "Your order #" + orderId + " status has been updated to: " + status;
        sendSuccessEmail(email, subject, message);
    }
}