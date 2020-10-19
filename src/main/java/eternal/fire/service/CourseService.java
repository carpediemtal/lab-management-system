package eternal.fire.service;

import eternal.fire.entity.Course;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseService {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Course> rowMapper = new BeanPropertyRowMapper<>(Course.class);

    public CourseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Course> getAllCourse() {
        return jdbcTemplate.query("select * from courses", rowMapper);
    }

    public void addCourse(String name, String time, String level, String week, String experimenter) {
        jdbcTemplate.update("insert into courses (name, time, week, level, experimenter) values (?, ?, ?, ?, ?)", name, time, level, week, experimenter);
    }

    public void deleteCourse(int id) {
        jdbcTemplate.update("delete from courses where id = ?", id);
    }

    public void arrangeCourse(String _class, String courseName) {
        int courseId = jdbcTemplate.queryForObject("select id from courses where name = ?", new Object[]{courseName}, ((resultSet, i) -> resultSet.getInt(1)));
        arrangeCourse(_class, courseId);
    }

    public void arrangeCourse(String _class, int courseId) {
        jdbcTemplate.update("insert into class_course (_class, course_id) VALUES (?,?)", _class, courseId);
    }

    public int getIdByName(String name) {
        return jdbcTemplate.queryForObject("select id from courses where name = ?", new Object[]{name}, (resultSet, i) -> resultSet.getInt(1));
    }

    public Course getCourseById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM courses WHERE id = ?", new Object[]{id}, rowMapper);
    }
}
