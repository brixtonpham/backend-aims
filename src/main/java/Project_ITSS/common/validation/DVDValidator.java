package Project_ITSS.common.validation;

import Project_ITSS.common.entity.DVD;
import Project_ITSS.common.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Concrete validator for DVD products
 * Implements DVD-specific validation rules
 */
@Component
public class DVDValidator extends BaseProductValidator {
    
    @Override
    public void validateForCreation(Product product) {
        validateCommonFields(product);
        validateDVDSpecificFields(product);
    }
    
    @Override
    public void validateForUpdate(Product product) {
        validateForUpdateCommon(product);
        validateDVDSpecificFields(product);
        validateDVDUpdateFields(product);
    }
    
    @Override
    public String getSupportedType() {
        return "dvd";
    }
    
    private void validateDVDSpecificFields(Product product) {
        if (!(product instanceof DVD)) {
            throw new ValidationException("Product type 'dvd' must be a DVD instance");
        }
        
        DVD dvd = (DVD) product;
        
        if (dvd.getDirector() == null || dvd.getDirector().trim().isEmpty()) {
            throw new ValidationException("DVD director is required");
        }
        
        if (dvd.getGenre() == null || dvd.getGenre().trim().isEmpty()) {
            throw new ValidationException("DVD genre is required");
        }
        
        if (dvd.getStudio() == null || dvd.getStudio().trim().isEmpty()) {
            throw new ValidationException("DVD studio is required");
        }
        
        if (dvd.getDvdType() == null || dvd.getDvdType().trim().isEmpty()) {
            throw new ValidationException("DVD type is required");
        }
    }
    
    private void validateDVDUpdateFields(Product product) {
        DVD dvd = (DVD) product;
        
        if (dvd.getDvdId() <= 0) {
            throw new ValidationException("DVD ID is required for update");
        }
    }
}