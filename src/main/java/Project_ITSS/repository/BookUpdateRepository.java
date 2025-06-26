package Project_ITSS.repository;

import Project_ITSS.common.entity.Book;
import Project_ITSS.common.entity.Product;
import Project_ITSS.common.util.DateUtils;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookUpdateRepository implements DetailProductUpdateRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private static final String UPDATE_BOOK_SQL = 
        "UPDATE Book SET genre = ?, page_count = ?, publication_date = ?, authors = ?, " +
        "publishers = ?, cover_type = ? WHERE product_id = ?";

    @Autowired
    public BookUpdateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateProductInfo(Product product) {
        if (!(product instanceof Book)) {
            throw new ValidationException("Product must be a Book instance for book update");
        }
        
        try {
            Book book = (Book) product;
            java.sql.Date sqlDate = DateUtils.parseStringToSqlDate(book.getPublicationDate());
            
            int rowsAffected = jdbcTemplate.update(
                UPDATE_BOOK_SQL,
                book.getGenre(),
                book.getPageCount(),
                sqlDate,
                book.getAuthors(),
                book.getPublishers(),
                book.getCoverType(),
                book.getProductId()
            );
            
            if (rowsAffected == 0) {
                throw new ValidationException("No book found with product ID: " + book.getProductId());
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to update book information: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "book";
    }
}