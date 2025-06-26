package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.util.DateUtils;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductAddRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private static final String INSERT_PRODUCT_SQL = 
        "INSERT INTO product (title, price, weight, rush_order_supported, image_url, barcode, import_date, introduction, quantity, type) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING product_id";

    @Autowired
    public ProductAddRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertProductInfo(Product product) {
        try {
            java.sql.Date sqlDate = DateUtils.parseStringToSqlDate(product.getImportDate());
            
            Integer productId = jdbcTemplate.queryForObject(
                INSERT_PRODUCT_SQL,
                Integer.class,
                product.getTitle(),
                product.getPrice(),
                product.getWeight(),
                product.isRushOrderSupported(),
                product.getImageUrl(),
                product.getBarcode(),
                sqlDate,
                product.getIntroduction(),
                product.getQuantity(),
                product.getType()
            );
            
            return productId != null ? productId : 0;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to insert product information: " + e.getMessage(), e);
        }
    }
}