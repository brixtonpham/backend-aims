package Project_ITSS.repository;

import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LoggerUpdateRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private static final String INSERT_LOGGER_SQL = 
        "INSERT INTO Logger (action_name, note, recorded_at) VALUES (?, ?, CURRENT_DATE)";
    
    private static final String COUNT_UPDATES_SQL = 
        "SELECT COUNT(DISTINCT note) FROM Logger WHERE recorded_at = CURRENT_DATE AND action_name = 'update product'";

    @Autowired
    public LoggerUpdateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveLogger(String actionName, String note) {
        try {
            jdbcTemplate.update(INSERT_LOGGER_SQL, actionName, note);
        } catch (Exception e) {
            throw new ValidationException("Failed to save logger: " + e.getMessage(), e);
        }
    }

    public int getUpdatingTimes() {
        try {
            Integer result = jdbcTemplate.queryForObject(COUNT_UPDATES_SQL, Integer.class);
            return result != null ? result : 0;
        } catch (Exception e) {
            throw new ValidationException("Failed to get updating times: " + e.getMessage(), e);
        }
    }
}