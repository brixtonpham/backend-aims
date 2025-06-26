package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductPlaceOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductPlaceOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getProductQuantity(int productId) {
        try {
            Integer quantity = jdbcTemplate.queryForObject(
                "SELECT quantity FROM Product WHERE product_id = ?", 
                new Object[]{productId}, 
                Integer.class
            );
            return quantity != null ? quantity : 0;
        } catch (Exception e) {
            throw new ValidationException("Failed to get product quantity: " + e.getMessage());
        }
    }

    public double getProductWeight(int productId) {
        try {
            Double weight = jdbcTemplate.queryForObject(
                "SELECT weight FROM Product WHERE product_id = ?", 
                new Object[]{productId}, 
                Double.class
            );
            return weight != null ? weight : 0.0;
        } catch (Exception e) {
            throw new ValidationException("Failed to get product weight: " + e.getMessage());
        }
    }

    public void updateProductQuantity(int orderId) {
        try {
            String sql = "UPDATE Product p " +
                        "SET quantity = p.quantity - ol.quantity " +
                        "FROM Orderline ol " +
                        "WHERE p.product_id = ol.product_id " +
                        "AND ol.order_id = ?";
            jdbcTemplate.update(sql, orderId);
        } catch (Exception e) {
            throw new ValidationException("Failed to update product quantity: " + e.getMessage());
        }
    }

    public Optional<Product> getProductById(long productId) {
        try {
            String sql = "SELECT * FROM Product WHERE product_id = ?";
            Product product = jdbcTemplate.queryForObject(sql, new Object[]{productId}, new BeanPropertyRowMapper<>(Product.class));
            return Optional.ofNullable(product);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Product> getAllProducts() {
        try {
            String sql = "SELECT * FROM Product";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class));
        } catch (Exception e) {
            throw new ValidationException("Failed to get all products: " + e.getMessage());
        }
    }

    public List<Product> getProductsByType(String type) {
        try {
            String sql = "SELECT * FROM Product WHERE type = ?";
            return jdbcTemplate.query(sql, new Object[]{type}, new BeanPropertyRowMapper<>(Product.class));
        } catch (Exception e) {
            throw new ValidationException("Failed to get products by type: " + e.getMessage());
        }
    }

    public List<Product> searchProductsByTitle(String title) {
        try {
            String sql = "SELECT * FROM Product WHERE title ILIKE ?";
            return jdbcTemplate.query(sql, new Object[]{"%" + title + "%"}, new BeanPropertyRowMapper<>(Product.class));
        } catch (Exception e) {
            throw new ValidationException("Failed to search products by title: " + e.getMessage());
        }
    }
}