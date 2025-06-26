package Project_ITSS.common.validation;

import Project_ITSS.common.entity.Book;
import Project_ITSS.common.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Concrete validator for Book products
 * Implements book-specific validation rules
 */
@Component
public class BookValidator extends BaseProductValidator {
    
    @Override
    public void validateForCreation(Product product) {
        validateCommonFields(product);
        validateBookSpecificFields(product);
    }
    
    @Override
    public void validateForUpdate(Product product) {
        validateForUpdateCommon(product);
        validateBookSpecificFields(product);
        validateBookUpdateFields(product);
    }
    
    @Override
    public String getSupportedType() {
        return "book";
    }
    
    private void validateBookSpecificFields(Product product) {
        if (!(product instanceof Book)) {
            throw new ValidationException("Product type 'book' must be a Book instance");
        }
        
        Book book = (Book) product;
        
        if (book.getAuthors() == null || book.getAuthors().trim().isEmpty()) {
            throw new ValidationException("Book authors are required");
        }
        
        if (book.getPageCount() <= 0) {
            throw new ValidationException("Book page count must be greater than 0");
        }
        
        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            throw new ValidationException("Book genre is required");
        }
        
        if (book.getPublishers() == null || book.getPublishers().trim().isEmpty()) {
            throw new ValidationException("Book publishers are required");
        }
    }
    
    private void validateBookUpdateFields(Product product) {
        Book book = (Book) product;
        
        if (book.getBookId() <= 0) {
            throw new ValidationException("Book ID is required for update");
        }
    }
}