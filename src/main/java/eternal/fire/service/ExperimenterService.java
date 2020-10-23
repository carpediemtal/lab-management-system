package eternal.fire.service;

import eternal.fire.entity.Course;
import eternal.fire.entity.Experimenter;
import eternal.fire.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExperimenterService {
    private static final Logger logger = LoggerFactory.getLogger(ExperimenterService.class);
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Experimenter> rowMapper = new BeanPropertyRowMapper<>(Experimenter.class);
    private final RowMapper<Student> studentRowMapper = new BeanPropertyRowMapper<>(Student.class);
    private final RowMapper<Course> courseRowMapper = new BeanPropertyRowMapper<>(Course.class);
    private final StudentService studentService;
    private final CourseService courseService;

    @Autowired
    public ExperimenterService(JdbcTemplate jdbcTemplate, StudentService studentService, CourseService courseService) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    public Experimenter getExperimenterByEmail(String email) {
        logger.info("try to get experimenter by email:{}", email);
        try {
            Experimenter experimenter = jdbcTemplate.queryForObject("select * from experimenter where email = ?", new Object[]{email}, rowMapper);
            assert experimenter != null;
            experimenter.setType("实验员");
            return experimenter;
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public List<Student> getMyStudent(String experimenter) {
        List<Integer> courseId = jdbcTemplate.query("select id from courses where experimenter = ?", new Object[]{experimenter}, ((resultSet, i) -> resultSet.getInt(1)));
        List<String> classes = new LinkedList<>();
        for (int id : courseId) {
            classes.addAll(jdbcTemplate.query("select _class from class_course where course_id = ?", new Object[]{id}, ((resultSet, i) -> resultSet.getString(1))));
        }
        List<Student> students = new LinkedList<>();
        for (String _class : classes) {
            students.addAll(jdbcTemplate.query("select name from student where _class = ?", new Object[]{_class}, studentRowMapper));
        }
        return students.stream().distinct().collect(Collectors.toList());
    }

    public List<Course> getMyCourse(String experimenter) {
        List<Integer> courseId = jdbcTemplate.query("select id from courses where experimenter = ?", new Object[]{experimenter}, ((resultSet, i) -> resultSet.getInt(1)));
        List<Course> courses = new LinkedList<>();
        for (int id : courseId) {
            courses.add(jdbcTemplate.queryForObject("select * from courses where id = ?", new Object[]{id}, courseRowMapper));
        }
        return courses;
    }

    public void recordScore(String name, String course, int score) {
        String studentId = studentService.getIdByName(name);
        int courseId = courseService.getIdByName(course);
        jdbcTemplate.update("insert into student_score (student_id, course_id, score) VALUES (?, ?, ?)", studentId, courseId, score);
    }
}
