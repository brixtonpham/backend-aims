package Project_ITSS.common.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory for creating appropriate validation strategies
 * Follows Factory Pattern + Strategy Pattern for extensibility
 */
@Component
public class ValidationStrategyFactory {
    
    private final Map<String, ProductValidator> validatorMap;
    
    @Autowired
    public ValidationStrategyFactory(List<ProductValidator> validators) {
        this.validatorMap = validators.stream()
            .collect(Collectors.toMap(
                validator -> validator.getSupportedType().toLowerCase(),
                Function.identity()
            ));
    }
    
    /**
     * Gets the appropriate validator for the given product type
     * @param productType the type of product (e.g., "book", "cd", "dvd")
     * @return the validator for that product type
     * @throws ValidationException if no validator found for the product type
     */
    public ProductValidator getValidator(String productType) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new ValidationException("Product type cannot be null or empty");
        }
        
        ProductValidator validator = validatorMap.get(productType.toLowerCase());
        if (validator == null) {
            throw new ValidationException("No validator found for product type: " + productType);
        }
        
        return validator;
    }
    
    /**
     * Checks if a validator exists for the given product type
     * @param productType the product type to check
     * @return true if validator exists, false otherwise
     */
    public boolean hasValidator(String productType) {
        return productType != null && validatorMap.containsKey(productType.toLowerCase());
    }
    
    /**
     * Gets all supported product types
     * @return set of supported product types
     */
    public java.util.Set<String> getSupportedTypes() {
        return validatorMap.keySet();
    }
}