package Project_ITSS.common.validation;

import Project_ITSS.common.entity.Product;

/**
 * Base validator with common validation logic
 * Reduces code duplication across concrete validators
 */
public abstract class BaseProductValidator implements ProductValidator {
    
    /**
     * Performs common product validation that applies to all product types
     * @param product the product to validate
     */
    protected void validateCommonFields(Product product) {
        if (product == null) {
            throw new ValidationException("Product cannot be null");
        }
        
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new ValidationException("Product title is required");
        }
        
        if (product.getPrice() <= 0) {
            throw new ValidationException("Product price must be greater than 0");
        }
        
        if (product.getWeight() <= 0) {
            throw new ValidationException("Product weight must be greater than 0");
        }
        
        if (product.getQuantity() < 0) {
            throw new ValidationException("Product quantity cannot be negative");
        }
        
        if (product.getType() == null || product.getType().trim().isEmpty()) {
            throw new ValidationException("Product type is required");
        }
        
        if (!supports(product.getType())) {
            throw new ValidationException("Invalid product type: " + product.getType());
        }
    }
    
    /**
     * Validates fields required for update operations
     * @param product the product to validate
     */
    protected void validateForUpdateCommon(Product product) {
        validateCommonFields(product);
        
        if (product.getProductId() <= 0) {
            throw new ValidationException("Product ID is required for update");
        }
    }
}