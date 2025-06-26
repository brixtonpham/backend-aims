package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LoggerAddRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private static final String INSERT_LOGGER_SQL = 
        "INSERT INTO Logger (action_name, recorded_at, note) VALUES (?, CURRENT_DATE, ?)";
    
    private static final String COUNT_ADD_PRODUCTS_SQL = 
        "SELECT COUNT(DISTINCT note) FROM Logger WHERE recorded_at = CURRENT_DATE AND action_name = 'add product'";

    @Autowired
    public LoggerAddRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveLogger(String actionName, String note) {
        try {
            jdbcTemplate.update(INSERT_LOGGER_SQL, actionName, note);
        } catch (Exception e) {
            throw new ValidationException("Failed to save logger: " + e.getMessage(), e);
        }
    }

    public void saveLog(Product product) {
        try {
            String note = "added product with id: " + product.getProductId();
            jdbcTemplate.update(INSERT_LOGGER_SQL, "add product", note);
        } catch (Exception e) {
            throw new ValidationException("Failed to save product logger: " + e.getMessage(), e);
        }
    }

    public boolean checkValidAddProducts() {
        try {
            Integer count = jdbcTemplate.queryForObject(COUNT_ADD_PRODUCTS_SQL, Integer.class);
            return count != null && count <= 30;
        } catch (Exception e) {
            throw new ValidationException("Failed to check add products validity: " + e.getMessage(), e);
        }
    }

    public boolean checkValidProducts() {
        return checkValidAddProducts();
    }
}