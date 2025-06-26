package Project_ITSS.repository;

import Project_ITSS.common.entity.Book;
import Project_ITSS.common.entity.Product;
import Project_ITSS.common.util.DateUtils;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookAddRepository implements DetailProductAddRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private static final String INSERT_BOOK_SQL = 
        "INSERT INTO Book (product_id, genre, page_count, publication_date, authors, publishers, cover_type) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Autowired
    public BookAddRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertProductInfo(Product product) {
        if (!(product instanceof Book)) {
            throw new ValidationException("Product must be a Book instance for book creation");
        }
        
        try {
            Book book = (Book) product;
            java.sql.Date sqlDate = DateUtils.parseStringToSqlDate(book.getPublicationDate());
            
            int rowsAffected = jdbcTemplate.update(
                INSERT_BOOK_SQL,
                book.getProductId(),
                book.getGenre(),
                book.getPageCount(),
                sqlDate,
                book.getAuthors(),
                book.getPublishers(),
                book.getCoverType()
            );
            
            if (rowsAffected == 0) {
                throw new ValidationException("Failed to insert book details");
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to insert book information: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "book";
    }
}