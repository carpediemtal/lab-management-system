package eternal.fire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LabController {

    @GetMapping("/test")
    public String labManagement(Model model, HttpSession session) {
        return "redirect:https://baidu.com";
    }

}
