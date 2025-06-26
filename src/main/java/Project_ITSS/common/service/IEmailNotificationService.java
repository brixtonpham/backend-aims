package Project_ITSS.common.service;

public interface IEmailNotificationService {
    
    void sendSuccessEmail(String email, String subject, String message);
    
    void sendErrorEmail(String email, String subject, String message);
    
    void sendOrderConfirmationEmail(String email, long orderId);
    
    void sendOrderCancellationEmail(String email, long orderId);
    
    void sendOrderStatusUpdateEmail(String email, long orderId, String status);
}