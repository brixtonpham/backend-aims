package Project_ITSS.common.validation;

import Project_ITSS.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Unified validation service using Strategy Pattern
 * Replaces scattered validation logic across modules
 */
@Service
public class ProductValidationService {
    
    private final ValidationStrategyFactory validationFactory;
    
    @Autowired
    public ProductValidationService(ValidationStrategyFactory validationFactory) {
        this.validationFactory = validationFactory;
    }
    
    /**
     * Validates a product for creation operations
     * @param product the product to validate
     * @throws ValidationException if validation fails
     */
    public void validateForCreation(Product product) {
        if (product == null) {
            throw new ValidationException("Product cannot be null");
        }
        
        ProductValidator validator = validationFactory.getValidator(product.getType());
        validator.validateForCreation(product);
    }
    
    /**
     * Validates a product for update operations
     * @param product the product to validate
     * @throws ValidationException if validation fails
     */
    public void validateForUpdate(Product product) {
        if (product == null) {
            throw new ValidationException("Product cannot be null");
        }
        
        ProductValidator validator = validationFactory.getValidator(product.getType());
        validator.validateForUpdate(product);
    }
    
    /**
     * Checks if validation is supported for the given product type
     * @param productType the product type to check
     * @return true if validation is supported, false otherwise
     */
    public boolean isValidationSupported(String productType) {
        return validationFactory.hasValidator(productType);
    }
    
    /**
     * Gets all supported product types for validation
     * @return set of supported product types
     */
    public java.util.Set<String> getSupportedProductTypes() {
        return validationFactory.getSupportedTypes();
    }
}