package Project_ITSS.repository;

import Project_ITSS.dto.OrderInfo;
import Project_ITSS.entity.Order;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderPlaceOrderRepository {
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderPlaceOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveOrder(Order order) {
        try {
            String sql = "INSERT INTO \"Order\" (order_id, delivery_id, Total_before_VAT, Total_after_VAT, status, VAT, order_time, payment_method) " +
                        "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, ?)";
            jdbcTemplate.update(sql,
                order.getOrder_id(),
                order.getDelivery_id(),
                order.getTotal_before_VAT(),
                order.getTotal_after_VAT(),
                order.getStatus(),
                order.getVAT(),
                order.getPayment_method()
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to save order: " + e.getMessage());
        }
    }

    public Optional<Order> getOrderById(long orderId) {
        try {
            String sql = "SELECT * FROM \"Order\" WHERE order_id = ?";
            Order order = jdbcTemplate.queryForObject(sql, new Object[]{orderId}, new BeanPropertyRowMapper<>(Order.class));
            return Optional.ofNullable(order);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Order> getAllOrders() {
        try {
            String sql = "SELECT * FROM \"Order\"";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class));
        } catch (Exception e) {
            throw new ValidationException("Failed to get all orders: " + e.getMessage());
        }
    }

    public List<Order> getOrdersByStatus(String status) {
        try {
            String sql = "SELECT * FROM \"Order\" WHERE status = ?";
            return jdbcTemplate.query(sql, new Object[]{status}, new BeanPropertyRowMapper<>(Order.class));
        } catch (Exception e) {
            throw new ValidationException("Failed to get orders by status: " + e.getMessage());
        }
    }

    public void updateOrder(Order order) {
        try {
            String sql = "UPDATE \"Order\" SET delivery_id = ?, Total_before_VAT = ?, Total_after_VAT = ?, " +
                        "status = ?, VAT = ?, payment_method = ? WHERE order_id = ?";
            jdbcTemplate.update(sql,
                order.getDelivery_id(),
                order.getTotal_before_VAT(),
                order.getTotal_after_VAT(),
                order.getStatus(),
                order.getVAT(),
                order.getPayment_method(),
                order.getOrder_id()
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to update order: " + e.getMessage());
        }
    }

    public void cancelOrder(long orderId) {
        try {
            String sql = "UPDATE \"Order\" SET status = 'cancelled' WHERE order_id = ?";
            jdbcTemplate.update(sql, orderId);
        } catch (Exception e) {
            throw new ValidationException("Failed to cancel order: " + e.getMessage());
        }
    }

    public boolean existsById(long orderId) {
        try {
            String sql = "SELECT COUNT(*) FROM \"Order\" WHERE order_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{orderId}, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            throw new ValidationException("Failed to check order existence: " + e.getMessage());
        }
    }

    public List<OrderInfo> getAllOrderInfos() {
        try {
            String sql = "SELECT order_id, name as customer_name, order_time as order_date, " +
                        "total_after_vat as total_amount, status, payment_method " +
                        "FROM \"Order\" JOIN deliveryinformation USING (delivery_id)";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(OrderInfo.class));
        } catch (Exception e) {
            throw new ValidationException("Failed to get all order infos: " + e.getMessage());
        }
    }
}