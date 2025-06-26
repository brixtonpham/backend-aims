package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductViewRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductViewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getProductQuantity(int productId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT quantity FROM Product WHERE product_id = ?", 
                new Object[]{productId}, 
                Integer.class
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to get product quantity: " + e.getMessage());
        }
    }

    public int getProductPrice(int productId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT price FROM Product WHERE product_id = ?", 
                new Object[]{productId}, 
                Integer.class
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to get product price: " + e.getMessage());
        }
    }

    public double getProductWeight(int productId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT weight FROM Product WHERE product_id = ?", 
                new Object[]{productId}, 
                Double.class
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to get product weight: " + e.getMessage());
        }
    }

    public Product findById(long id) {
        try {
            String sql = "SELECT * FROM product WHERE product_id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ProductViewRowMapper());
        } catch (Exception e) {
            throw new ValidationException("Failed to find product by id: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        try {
            String sql = "SELECT * FROM product";
            return jdbcTemplate.query(sql, new ProductViewRowMapper());
        } catch (Exception e) {
            throw new ValidationException("Failed to get all products: " + e.getMessage());
        }
    }
}