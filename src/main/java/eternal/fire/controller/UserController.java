package eternal.fire.controller;

import eternal.fire.entity.Course;
import eternal.fire.entity.User;
import eternal.fire.service.CourseService;
import eternal.fire.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String KEY_USER_EMAIL = "__userEmail__";
    private final UserService userService;
    private final CourseService courseService;

    public UserController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    public User getUserFromSession(HttpSession session) {
        String email = (String) session.getAttribute(KEY_USER_EMAIL);
        System.out.println("从session中取出来的email是：" + email);
        return userService.getUserByEmail(email);
    }

    // 主页面、登录、登出
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "index";
    }

    @GetMapping("/sign_in")
    public String signIn(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        model.addAttribute("user", user);
        model.addAttribute("error", null);
        return user == null ? "signIn" : "redirect:/profile";
    }

    @PostMapping("/sign_in")
    public String doSignIn(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session, RedirectAttributes redirectAttributes) {
        logger.info("try to DoSignIn by email and password:{},{}", email, password);

        try {
            User user = userService.signIn(email, password);
            System.out.println(user.getEmail());
            session.setAttribute(KEY_USER_EMAIL, user.getEmail());
        } catch (Exception e) {
            logger.info("登录失败");
            redirectAttributes.addFlashAttribute("error", "邮箱和密码不匹配");
            return "redirect:/sign_in";
        }
        return "redirect:/profile";
    }

    @GetMapping("/sign_out")
    public String signOut(HttpSession session) {
        session.removeAttribute(KEY_USER_EMAIL);
        return "redirect:/";
    }


    // 个人信息和功能
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/sign_in";
        }
        model.addAttribute("user", user);
        String type = user.getType();
        switch (type) {
            case "学生" -> model.addAttribute("flag", 1);
            case "实验员" -> model.addAttribute("flag", 2);
            case "管理员" -> model.addAttribute("flag", 3);
        }
        return "profile";
    }


    // 学生功能
    @GetMapping("/score_query")
    public String scoreQuery(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/sign_in";
        }
        model.addAttribute("user", user);
        if (!user.getType().equals("学生")) {
            return "redirect:/profile";
        }
        model.addAttribute("courses", userService.studentService.getMyCourse(user.getName()));
        return "scoreQuery";
    }

    @GetMapping("/course_query")
    public String courseQuery(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/sign_in";
        }
        model.addAttribute("user", user);
        List<Course> courses = userService.studentService.getMyCourse(user.getName());
        model.addAttribute("courses", courses);
        return "courseQuery";
    }


    // 实验员功能
    @GetMapping("/record_score")
    public String recordScore(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/sign_in";
        }
        model.addAttribute("user", user);
        var classes = userService.studentService.getClasses();
        System.out.println(classes);
        model.addAttribute("classes", userService.studentService.getClasses());
        if (!user.getType().equals("实验员")) {
            return "redirect:/profile";
        }
        model.addAttribute("students", userService.experimenterService.getMyStudent(user.getName()));
        model.addAttribute("courses", userService.experimenterService.getMyCourse(user.getName()));
//        model.addAttribute("info", null);
        return "recordScore";
    }

    @PostMapping("/record_score")
    public String recordScore(@RequestParam String name, @RequestParam String course, @RequestParam int score, RedirectAttributes redirectAttributes) {
        userService.experimenterService.recordScore(name, course, score);
        redirectAttributes.addFlashAttribute("info", "登记成功");
        return "redirect:/record_score";
    }


    // 管理员功能
    @GetMapping("/lab_management")
    public String labManagement(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            logger.info("User is null");

            return "redirect:/sign_in";
        }
        model.addAttribute("user", user);
        model.addAttribute("info", "");
        if (!user.getType().equals("管理员")) {
            return "redirect:/profile";
        }

        var courses = courseService.getAllCourse();
        model.addAttribute("courses", courses);
        return "labManagement";
    }

    @PostMapping("/lab_management")
    public String labManagement(@RequestParam("name") String name, @RequestParam("time") String time, @RequestParam("level") String level, @RequestParam("week") String week, @RequestParam("experimenter") String experimenter) {
        courseService.addCourse(name, time, level, week, experimenter);
        return "redirect:/lab_management";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") int id) {
        System.out.println("主键：" + id);
        courseService.deleteCourse(id);
        return "redirect:/lab_management";
    }

    @GetMapping("/lab_arrangement")
    public String labArrangement(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            logger.info("User is null");
            return "redirect:/sign_in";
        }
        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.getAllCourse());
        var classes = userService.studentService.getClasses();
        model.addAttribute("classes", userService.studentService.getClasses());
        return "labArrangement";
    }

    @PostMapping("/lab_arrangement")
    public String arrangeCourse(@RequestParam("class") String _class, @RequestParam("course") String course, RedirectAttributes redirectAttributes) {
        try {
            courseService.arrangeCourse(_class, course);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("info", "提交失败");
        }
        redirectAttributes.addFlashAttribute("info", "提交成功");
        return "redirect:/lab_arrangement";
    }
}
