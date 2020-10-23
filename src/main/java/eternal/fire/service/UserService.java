package eternal.fire.service;

import eternal.fire.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final AdministratorService administratorService;
    public final ExperimenterService experimenterService;
    public final StudentService studentService;

    @Autowired
    public UserService(AdministratorService administratorService, ExperimenterService experimenterService, StudentService studentService) {
        this.administratorService = administratorService;
        this.experimenterService = experimenterService;
        this.studentService = studentService;
    }

    // 在三张表里用Email找User，找到了就返回，没找到就返回null
    public User getUserByEmail(String email) {
        logger.info("try to get user by email:{}", email);
        User user = studentService.getStudentByEmail(email);
        if (user != null) {
            return user;
        }
        user = experimenterService.getExperimenterByEmail(email);
        if (user != null) {
            return user;
        }
        user = administratorService.getAdministratorByEmail(email);
        return user;
    }

    public User signIn(String email, String password) {
        logger.info("try to sign in by email and password:{},{}", email, password);
        User user = getUserByEmail(email);
        if (user.getPassword().equals(password)) {
            return user;
        } else {
            throw new RuntimeException("账号密码不匹配");
        }
    }
}
