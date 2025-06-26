package Project_ITSS.repository;

import Project_ITSS.common.entity.DVD;
import Project_ITSS.common.entity.Product;
import Project_ITSS.common.util.DateUtils;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DVDAddRepository implements DetailProductAddRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private static final String INSERT_DVD_SQL = 
        "INSERT INTO DVD (Product_id, title, release_Date, DVD_type, genre, studio, director) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Autowired
    public DVDAddRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertProductInfo(Product product) {
        if (!(product instanceof DVD)) {
            throw new ValidationException("Product must be a DVD instance for DVD creation");
        }
        
        try {
            DVD dvd = (DVD) product;
            java.sql.Date sqlDate = DateUtils.parseStringToSqlDate(dvd.getReleaseDate());
            
            int rowsAffected = jdbcTemplate.update(
                INSERT_DVD_SQL,
                dvd.getProductId(),
                dvd.getTitle(),
                sqlDate,
                dvd.getDvdType(),
                dvd.getGenre(),
                dvd.getStudio(),
                dvd.getDirector()
            );
            
            if (rowsAffected == 0) {
                throw new ValidationException("Failed to insert DVD details");
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to insert DVD information: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "dvd";
    }
}