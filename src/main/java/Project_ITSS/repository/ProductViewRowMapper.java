package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductViewRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getLong("product_id"));
        product.setTitle(rs.getString("title"));
        product.setPrice(rs.getInt("price"));
        product.setWeight(rs.getFloat("weight"));
        product.setRushOrderSupported(rs.getBoolean("rush_order_supported"));
        product.setImageUrl(rs.getString("image_url"));
        product.setBarcode(rs.getString("barcode"));
        product.setImportDate(rs.getString("import_date"));
        product.setIntroduction(rs.getString("introduction"));
        product.setQuantity(rs.getInt("quantity"));
        product.setType(rs.getString("type"));
        return product;
    }
}