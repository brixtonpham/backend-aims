package Project_ITSS.repository;

import Project_ITSS.common.entity.DVD;
import Project_ITSS.common.entity.Product;
import Project_ITSS.common.util.DateUtils;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DVDUpdateRepository implements DetailProductUpdateRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private static final String UPDATE_DVD_SQL = 
        "UPDATE DVD SET release_date = ?, dvd_type = ?, genre = ?, studio = ?, " +
        "director = ? WHERE product_id = ?";

    @Autowired
    public DVDUpdateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateProductInfo(Product product) {
        if (!(product instanceof DVD)) {
            throw new ValidationException("Product must be a DVD instance for DVD update");
        }
        
        try {
            DVD dvd = (DVD) product;
            java.sql.Date sqlDate = DateUtils.parseStringToSqlDate(dvd.getReleaseDate());
            
            int rowsAffected = jdbcTemplate.update(
                UPDATE_DVD_SQL,
                sqlDate,
                dvd.getDvdType(),
                dvd.getGenre(),
                dvd.getStudio(),
                dvd.getDirector(),
                dvd.getProductId()
            );
            
            if (rowsAffected == 0) {
                throw new ValidationException("No DVD found with product ID: " + dvd.getProductId());
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to update DVD information: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "dvd";
    }
}