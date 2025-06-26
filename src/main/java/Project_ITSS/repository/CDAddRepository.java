package Project_ITSS.repository;

import Project_ITSS.common.entity.CD;
import Project_ITSS.common.entity.Product;
import Project_ITSS.common.util.DateUtils;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CDAddRepository implements DetailProductAddRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private static final String INSERT_CD_SQL = 
        "INSERT INTO CD (Product_id, Track_list, genre, record_label, artists, release_date) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    @Autowired
    public CDAddRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertProductInfo(Product product) {
        if (!(product instanceof CD)) {
            throw new ValidationException("Product must be a CD instance for CD creation");
        }
        
        try {
            CD cd = (CD) product;
            java.sql.Date sqlDate = DateUtils.parseStringToSqlDate(cd.getReleaseDate());
            
            int rowsAffected = jdbcTemplate.update(
                INSERT_CD_SQL,
                cd.getProductId(),
                cd.getTrackList(),
                cd.getGenre(),
                cd.getRecordLabel(),
                cd.getArtists(),
                sqlDate
            );
            
            if (rowsAffected == 0) {
                throw new ValidationException("Failed to insert CD details");
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to insert CD information: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "cd";
    }
}