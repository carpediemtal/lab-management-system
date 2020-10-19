package eternal.fire.service;

import eternal.fire.entity.Administrator;
import eternal.fire.entity.Experimenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class AdministratorService {
    private static final Logger logger = LoggerFactory.getLogger(AdministratorService.class);
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Administrator> rowMapper = new BeanPropertyRowMapper<>(Administrator.class);

    @Autowired
    public AdministratorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Administrator getAdministratorByEmail(String email) {
        logger.info("try to get Administrator by email:{}", email);
        try {
            Administrator administrator = jdbcTemplate.queryForObject("select * from administrator where email = ?", new Object[]{email}, rowMapper);
            assert administrator != null;
            administrator.setType("管理员");
            return administrator;
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }
}
