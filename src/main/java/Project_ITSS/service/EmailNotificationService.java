package Project_ITSS.service;

import Project_ITSS.common.service.IEmailNotificationService;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("mainEmailNotificationService")
@Primary
public class EmailNotificationService implements IEmailNotificationService {

    @Override
    public void sendSuccessEmail(String email, String subject, String message) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address cannot be null or empty");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new ValidationException("Email subject cannot be null or empty");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new ValidationException("Email message cannot be null or empty");
        }
        
        // Simulate email sending (in real implementation, this would use JavaMail or similar)
        System.out.println("Sending email to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("Email sent successfully!");
    }

    @Override
    public void sendErrorEmail(String email, String subject, String message) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address cannot be null or empty");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new ValidationException("Email subject cannot be null or empty");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new ValidationException("Email message cannot be null or empty");
        }
        
        // Simulate email sending (in real implementation, this would use JavaMail or similar)
        System.out.println("Sending failure notification email to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("Failure notification email sent successfully!");
    }

    @Override
    public void sendOrderConfirmationEmail(String email, long orderId) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address cannot be null or empty");
        }
        
        String subject = "Order Confirmation - Order #" + orderId;
        String message = "Thank you for your order! Your order #" + orderId + " has been confirmed and is being processed.";
        
        System.out.println("Sending order confirmation email to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("Order confirmation email sent successfully!");
    }

    @Override
    public void sendOrderCancellationEmail(String email, long orderId) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address cannot be null or empty");
        }
        
        String subject = "Order Cancellation - Order #" + orderId;
        String message = "Your order #" + orderId + " has been cancelled successfully.";
        
        System.out.println("Sending order cancellation email to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("Order cancellation email sent successfully!");
    }

    @Override
    public void sendOrderStatusUpdateEmail(String email, long orderId, String status) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address cannot be null or empty");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("Order status cannot be null or empty");
        }
        
        String subject = "Order Status Update - Order #" + orderId;
        String message = "Your order #" + orderId + " status has been updated to: " + status;
        
        System.out.println("Sending order status update email to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("Order status update email sent successfully!");
    }
}