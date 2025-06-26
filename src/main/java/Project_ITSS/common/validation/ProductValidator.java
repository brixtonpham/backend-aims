package Project_ITSS.common.validation;

import Project_ITSS.common.entity.Product;

/**
 * Strategy interface for product validation
 * Follows the Strategy Pattern to make validation extensible and maintainable
 */
public interface ProductValidator {
    
    /**
     * Validates a product for creation/addition operations
     * @param product the product to validate
     * @throws ValidationException if validation fails
     */
    void validateForCreation(Product product);
    
    /**
     * Validates a product for update operations
     * @param product the product to validate
     * @throws ValidationException if validation fails
     */
    void validateForUpdate(Product product);
    
    /**
     * Returns the product type this validator handles
     * @return the product type (e.g., "book", "cd", "dvd")
     */
    String getSupportedType();
    
    /**
     * Checks if this validator supports the given product type
     * @param productType the product type to check
     * @return true if this validator supports the type, false otherwise
     */
    default boolean supports(String productType) {
        return getSupportedType().equalsIgnoreCase(productType);
    }
}