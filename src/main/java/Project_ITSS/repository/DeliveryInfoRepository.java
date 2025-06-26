package Project_ITSS.repository;

import Project_ITSS.entity.DeliveryInformation;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DeliveryInfoRepository {
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DeliveryInfoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int saveDeliveryInfo(DeliveryInformation deliveryInfo) {
        try {
            String sql = "INSERT INTO DeliveryInformation (name, phone, email, address, province, delivery_message, delivery_fee) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, deliveryInfo.getName());
                ps.setString(2, deliveryInfo.getPhone());
                ps.setString(3, deliveryInfo.getEmail());
                ps.setString(4, deliveryInfo.getAddress());
                ps.setString(5, deliveryInfo.getProvince());
                ps.setString(6, deliveryInfo.getDelivery_message());
                ps.setInt(7, deliveryInfo.getDelivery_fee());
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            return key != null ? key.intValue() : -1;
        } catch (Exception e) {
            throw new ValidationException("Failed to save delivery information: " + e.getMessage());
        }
    }

    public Optional<DeliveryInformation> getDeliveryInfoById(int deliveryId) {
        try {
            String sql = "SELECT * FROM DeliveryInformation WHERE delivery_id = ?";
            DeliveryInformation deliveryInfo = jdbcTemplate.queryForObject(sql, new Object[]{deliveryId}, new BeanPropertyRowMapper<>(DeliveryInformation.class));
            return Optional.ofNullable(deliveryInfo);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<DeliveryInformation> getAllDeliveryInfos() {
        try {
            String sql = "SELECT * FROM DeliveryInformation";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DeliveryInformation.class));
        } catch (Exception e) {
            throw new ValidationException("Failed to get all delivery information: " + e.getMessage());
        }
    }

    public void updateDeliveryInfo(DeliveryInformation deliveryInfo) {
        try {
            String sql = "UPDATE DeliveryInformation SET name = ?, phone = ?, email = ?, address = ?, " +
                        "province = ?, delivery_message = ?, delivery_fee = ? WHERE delivery_id = ?";
            jdbcTemplate.update(sql,
                deliveryInfo.getName(),
                deliveryInfo.getPhone(),
                deliveryInfo.getEmail(),
                deliveryInfo.getAddress(),
                deliveryInfo.getProvince(),
                deliveryInfo.getDelivery_message(),
                deliveryInfo.getDelivery_fee(),
                deliveryInfo.getDelivery_id()
            );
        } catch (Exception e) {
            throw new ValidationException("Failed to update delivery information: " + e.getMessage());
        }
    }

    public String getCustomerAddress(int order_id) {
        try {
            String sql = "SELECT address FROM \"Order\" JOIN DeliveryInformation USING(delivery_id) WHERE order_id = ?";
            return jdbcTemplate.queryForObject(sql, String.class, order_id);
        } catch (Exception e) {
            throw new ValidationException("Failed to get customer address: " + e.getMessage());
        }
    }

    public void deleteDeliveryInfo(int deliveryId) {
        try {
            String sql = "DELETE FROM DeliveryInformation WHERE delivery_id = ?";
            jdbcTemplate.update(sql, deliveryId);
        } catch (Exception e) {
            throw new ValidationException("Failed to delete delivery information: " + e.getMessage());
        }
    }
}