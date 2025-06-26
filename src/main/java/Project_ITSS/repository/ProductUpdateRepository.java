package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.util.DateUtils;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductUpdateRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private static final String UPDATE_PRODUCT_SQL = 
        "UPDATE Product SET title = ?, price = ?, weight = ?, rush_order_supported = ?, " +
        "image_url = ?, barcode = ?, import_date = ?, introduction = ?, quantity = ? " +
        "WHERE product_id = ?";

    @Autowired
    public ProductUpdateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateProductInfo(Product product) {
        try {
            java.sql.Date sqlDate = DateUtils.parseStringToSqlDate(product.getImportDate());
            
            int rowsAffected = jdbcTemplate.update(
                UPDATE_PRODUCT_SQL,
                product.getTitle(),
                product.getPrice(),
                product.getWeight(),
                product.isRushOrderSupported(),
                product.getImageUrl(),
                product.getBarcode(),
                sqlDate,
                product.getIntroduction(),
                product.getQuantity(),
                product.getProductId()
            );
            
            if (rowsAffected == 0) {
                throw new ValidationException("No product found with ID: " + product.getProductId());
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to update product information: " + e.getMessage(), e);
        }
    }
}