package Project_ITSS.repository;

import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserViewRepository {
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserViewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String verifyUserRole(int userId) {
        try {
            String sql = "SELECT role FROM User WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, String.class);
        } catch (Exception e) {
            throw new ValidationException("Failed to verify user role: " + e.getMessage());
        }
    }
}