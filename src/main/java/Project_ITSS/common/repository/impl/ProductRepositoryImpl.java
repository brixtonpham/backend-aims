package Project_ITSS.common.repository.impl;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.repository.IProductRepository;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("commonProductRepositoryImpl")
public class ProductRepositoryImpl implements IProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertProductInfo(Product product) {
        try {
            String sql = "INSERT INTO Product (title, price, weight, rush_order_supported, image_url, barcode, import_date, introduction, quantity) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            return jdbcTemplate.update(sql,
                product.getTitle(),
                product.getPrice(),
                product.getWeight(),
                product.isRushOrderSupported(),
                product.getImageUrl(),
                product.getBarcode(),
                product.getImportDate(),
                product.getIntroduction(),
                product.getQuantity()
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to insert product: " + e.getMessage());
        }
    }

    @Override
    public void updateProduct(Product product) {
        try {
            String sql = "UPDATE Product SET title = ?, price = ?, weight = ?, rush_order_supported = ?, " +
                        "image_url = ?, barcode = ?, import_date = ?, introduction = ?, quantity = ? WHERE product_id = ?";
            jdbcTemplate.update(sql,
                product.getTitle(),
                product.getPrice(),
                product.getWeight(),
                product.isRushOrderSupported(),
                product.getImageUrl(),
                product.getBarcode(),
                product.getImportDate(),
                product.getIntroduction(),
                product.getQuantity(),
                product.getProductId()
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to update product: " + e.getMessage());
        }
    }

    @Override
    public Optional<Product> findById(long productId) {
        try {
            String sql = "SELECT * FROM Product WHERE product_id = ?";
            Product product = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Product.class), productId);
            return Optional.ofNullable(product);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findAll() {
        try {
            String sql = "SELECT * FROM Product";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Product> findByType(String type) {
        try {
            String sql = "SELECT * FROM Product WHERE type = ?";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class), type);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public boolean checkProductValidity(int quantity, long productId) {
        try {
            String sql = "SELECT quantity FROM Product WHERE product_id = ?";
            Integer availableQuantity = jdbcTemplate.queryForObject(sql, Integer.class, productId);
            return availableQuantity != null && availableQuantity >= quantity;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteById(long productId) {
        try {
            String sql = "DELETE FROM Product WHERE product_id = ?";
            jdbcTemplate.update(sql, productId);
        } catch (Exception e) {
            throw new ValidationException("Failed to delete product: " + e.getMessage());
        }
    }

    @Override
    public boolean existsById(long productId) {
        try {
            String sql = "SELECT COUNT(*) FROM Product WHERE product_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, productId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Product> findByTitleContaining(String title) {
        try {
            String sql = "SELECT * FROM Product WHERE title LIKE ?";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class), "%" + title + "%");
        } catch (Exception e) {
            return List.of();
        }
    }
}
