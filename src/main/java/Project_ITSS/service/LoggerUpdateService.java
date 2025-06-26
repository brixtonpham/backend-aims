package Project_ITSS.service;

import Project_ITSS.common.entity.Product;
import Project_ITSS.repository.LoggerUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggerUpdateService {
    
    private final LoggerUpdateRepository loggerRepository;

    @Autowired
    public LoggerUpdateService(LoggerUpdateRepository loggerRepository) {
        this.loggerRepository = loggerRepository;
    }

    public void saveLogger(Product product) {
        try {
            loggerRepository.saveLogger("update product", "updated product with id: " + product.getProductId());
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to save logger: " + e.getMessage());
        }
    }

    public boolean checkValidUpdateProducts() {
        try {
            int times = loggerRepository.getUpdatingTimes();
            return times <= 30;
        } catch (Exception e) {
            // If logger check fails, allow the operation to proceed
            System.err.println("Failed to check update times: " + e.getMessage());
            return true;
        }
    }
}