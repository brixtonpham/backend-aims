package Project_ITSS.repository;

import Project_ITSS.entity.Orderline;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.dto.ProductItem;
import Project_ITSS.repository.mapper.ProductItemRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderlineRepository {
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderlineRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveOrderline(Orderline orderline) {
        try {
            String sql = "INSERT INTO Orderline (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                orderline.getOrder_id(),
                orderline.getProduct_id(),
                orderline.getQuantity(),
                orderline.getPrice()
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to save orderline: " + e.getMessage());
        }
    }

    public List<Orderline> getOrderlinesByOrderId(long orderId) {
        try {
            String sql = "SELECT * FROM Orderline WHERE order_id = ?";
            return jdbcTemplate.query(sql, new Object[]{orderId}, new BeanPropertyRowMapper<>(Orderline.class));
        } catch (Exception e) {
            throw new ValidationException("Failed to get orderlines by order ID: " + e.getMessage());
        }
    }

    public void updateOrderline(Orderline orderline) {
        try {
            String sql = "UPDATE Orderline SET quantity = ?, price = ? WHERE order_id = ? AND product_id = ?";
            jdbcTemplate.update(sql,
                orderline.getQuantity(),
                orderline.getPrice(),
                orderline.getOrder_id(),
                orderline.getProduct_id()
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to update orderline: " + e.getMessage());
        }
    }

    public List<ProductItem> getProductItemByOrderId(int order_id) {
        try {
            String sql = "SELECT ol.*,p.title,p.price FROM Product p JOIN Orderline ol USING(product_id) JOIN \"Order\" o USING(order_id) WHERE o.order_id = ?";
            return jdbcTemplate.query(sql, new ProductItemRowMapper(), order_id);
        } catch (Exception e) {
            throw new ValidationException("Failed to get product items by order ID: " + e.getMessage());
        }
    }

    public void deleteOrderlinesByOrderId(long orderId) {
        try {
            String sql = "DELETE FROM Orderline WHERE order_id = ?";
            jdbcTemplate.update(sql, orderId);
        } catch (Exception e) {
            throw new ValidationException("Failed to delete orderlines: " + e.getMessage());
        }
    }
}