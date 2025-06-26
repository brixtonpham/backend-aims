package Project_ITSS.common.repository.impl;

import Project_ITSS.entity.Order;
import Project_ITSS.common.repository.IOrderRepository;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("commonOrderRepositoryImpl")
public class OrderRepositoryImpl implements IOrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
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

    @Override
    public Optional<Order> findById(long orderId) {
        try {
            String sql = "SELECT * FROM \"Order\" WHERE order_id = ?";
            Order order = jdbcTemplate.queryForObject(sql, new Object[]{orderId}, new BeanPropertyRowMapper<>(Order.class));
            return Optional.ofNullable(order);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findAll() {
        try {
            String sql = "SELECT * FROM \"Order\"";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Order> findByStatus(String status) {
        try {
            String sql = "SELECT * FROM \"Order\" WHERE status = ?";
            return jdbcTemplate.query(sql, new Object[]{status}, new BeanPropertyRowMapper<>(Order.class));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public void updateOrder(Order order) {
        try {
            String sql = "UPDATE \"Order\" SET delivery_id = ?, Total_before_VAT = ?, Total_after_VAT = ?, status = ?, VAT = ?, payment_method = ? WHERE order_id = ?";
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

    @Override
    public void deleteById(long orderId) {
        try {
            String sql = "DELETE FROM \"Order\" WHERE order_id = ?";
            jdbcTemplate.update(sql, orderId);
        } catch (Exception e) {
            throw new ValidationException("Failed to delete order: " + e.getMessage());
        }
    }

    @Override
    public boolean existsById(long orderId) {
        try {
            String sql = "SELECT COUNT(*) FROM \"Order\" WHERE order_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{orderId}, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Order> findByDeliveryId(int deliveryId) {
        try {
            String sql = "SELECT * FROM \"Order\" WHERE delivery_id = ?";
            return jdbcTemplate.query(sql, new Object[]{deliveryId}, new BeanPropertyRowMapper<>(Order.class));
        } catch (Exception e) {
            return List.of();
        }
    }
}
