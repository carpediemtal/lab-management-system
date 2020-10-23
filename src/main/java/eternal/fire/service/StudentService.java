package eternal.fire.service;

import eternal.fire.entity.Course;
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

@Component
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Student> rowMapper = new BeanPropertyRowMapper<>(Student.class);
    private final CourseService courseService;

    @Autowired
    public StudentService(JdbcTemplate jdbcTemplate, CourseService courseService) {
        this.jdbcTemplate = jdbcTemplate;
        this.courseService = courseService;
    }

    public Student getStudentByEmail(String email) {
        logger.info("try to get student by email:{}", email);
        try {
            Student student = jdbcTemplate.queryForObject("select * from student where email = ?", new Object[]{email}, rowMapper);
            assert student != null;
            student.setType("学生");
            return student;
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public String getIdByName(String name) {
        return jdbcTemplate.queryForObject("select id from student where name = ?", new Object[]{name}, (resultSet, i) -> resultSet.getString(1));
    }

    public Student singIn(String email, String password) {
        logger.info("try to sign in by {} and {}", email, password);
        Student student = getStudentByEmail(email);
        if (student.getPassword().equals(password)) {
            return student;
        } else {
            throw new RuntimeException("Sign in failed for wrong email or password");
        }
    }

    public List<String> getClasses() {
        return jdbcTemplate.query("select distinct _class from student", (resultSet, i) -> resultSet.getString(1));
    }

    public List<Course> getMyCourse(String name) {
        String studentId = getIdByName(name);
        List<Integer> coursesId = jdbcTemplate.query("SELECT course_id FROM student_score WHERE student_id = ?", new Object[]{studentId}, (resultSet, i) -> resultSet.getInt(1));
        List<Course> courses = new LinkedList<>();
        for (int id : coursesId) {
            courses.add(courseService.getCourseById(id));
        }
        return courses;
    }

    public List<Integer> getScoreByCourses(List<Course> courses, String studentId) {
        List<Integer> scores = new LinkedList<>();
        for (Course course : courses) {
            Integer score = jdbcTemplate.queryForObject("SELECT score FROM student_score WHERE student_id = ? AND course_id = ?", new Object[]{studentId, course.getId()}, ((resultSet, i) -> resultSet.getInt(1)));
            scores.add(score);
        }
        return scores;
    }
}
