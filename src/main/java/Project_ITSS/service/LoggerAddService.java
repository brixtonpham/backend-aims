package Project_ITSS.service;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.repository.LoggerAddRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggerAddService {
    
    private final LoggerAddRepository loggerRepository;

    @Autowired
    public LoggerAddService(LoggerAddRepository loggerRepository) {
        this.loggerRepository = loggerRepository;
    }

    public void saveLogger(Product product) {
        if (product == null) {
            throw new ValidationException("Product cannot be null for logging");
        }
        try {
            loggerRepository.saveLog(product);
        } catch (Exception e) {
            // Re-throw as runtime exception to allow service tests to catch it
            throw new RuntimeException("Failed to save logger: " + e.getMessage(), e);
        }
    }
    
    public void saveLogger(String action, String note) {
        try {
            loggerRepository.saveLogger(action, note);
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to save logger: " + e.getMessage());
        }
    }

    public boolean checkValidAddProducts() {
        try {
            return loggerRepository.checkValidProducts();
        } catch (Exception e) {
            // Re-throw to allow tests to catch exceptions
            throw new RuntimeException("Failed to check add products validity: " + e.getMessage(), e);
        }
    }
}