package Project_ITSS.repository;

import Project_ITSS.common.entity.CD;
import Project_ITSS.common.entity.Product;
import Project_ITSS.common.util.DateUtils;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CDUpdateRepository implements DetailProductUpdateRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private static final String UPDATE_CD_SQL = 
        "UPDATE CD SET track_list = ?, genre = ?, record_label = ?, artists = ?, " +
        "release_date = ? WHERE product_id = ?";

    @Autowired
    public CDUpdateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateProductInfo(Product product) {
        if (!(product instanceof CD)) {
            throw new ValidationException("Product must be a CD instance for CD update");
        }
        
        try {
            CD cd = (CD) product;
            java.sql.Date sqlDate = DateUtils.parseStringToSqlDate(cd.getReleaseDate());
            
            int rowsAffected = jdbcTemplate.update(
                UPDATE_CD_SQL,
                cd.getTrackList(),
                cd.getGenre(),
                cd.getRecordLabel(),
                cd.getArtists(),
                sqlDate,
                cd.getProductId()
            );
            
            if (rowsAffected == 0) {
                throw new ValidationException("No CD found with product ID: " + cd.getProductId());
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to update CD information: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "cd";
    }
}