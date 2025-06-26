package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DVDViewRepository implements DetailProductViewRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DVDViewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Product getProductDetailInfo(int productId) {
        try {
            String sql = "SELECT * FROM DVD JOIN Product USING (product_id) WHERE product_id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{productId}, new DVDViewRowMapper());
        } catch (Exception e) {
            throw new ValidationException("Failed to get DVD details: " + e.getMessage());
        }
    }

    @Override
    public String getType() {
        return "dvd";
    }
}