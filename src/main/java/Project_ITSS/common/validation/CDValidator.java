package Project_ITSS.common.validation;

import Project_ITSS.common.entity.CD;
import Project_ITSS.common.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Concrete validator for CD products
 * Implements CD-specific validation rules
 */
@Component
public class CDValidator extends BaseProductValidator {
    
    @Override
    public void validateForCreation(Product product) {
        validateCommonFields(product);
        validateCDSpecificFields(product);
    }
    
    @Override
    public void validateForUpdate(Product product) {
        validateForUpdateCommon(product);
        validateCDSpecificFields(product);
        validateCDUpdateFields(product);
    }
    
    @Override
    public String getSupportedType() {
        return "cd";
    }
    
    private void validateCDSpecificFields(Product product) {
        if (!(product instanceof CD)) {
            throw new ValidationException("Product type 'cd' must be a CD instance");
        }
        
        CD cd = (CD) product;
        
        if (cd.getArtists() == null || cd.getArtists().trim().isEmpty()) {
            throw new ValidationException("CD artists are required");
        }
        
        if (cd.getGenre() == null || cd.getGenre().trim().isEmpty()) {
            throw new ValidationException("CD genre is required");
        }
        
        if (cd.getRecordLabel() == null || cd.getRecordLabel().trim().isEmpty()) {
            throw new ValidationException("CD record label is required");
        }
        
        if (cd.getTrackList() == null || cd.getTrackList().trim().isEmpty()) {
            throw new ValidationException("CD track list is required");
        }
    }
    
    private void validateCDUpdateFields(Product product) {
        CD cd = (CD) product;
        
        if (cd.getCdId() <= 0) {
            throw new ValidationException("CD ID is required for update");
        }
    }
}