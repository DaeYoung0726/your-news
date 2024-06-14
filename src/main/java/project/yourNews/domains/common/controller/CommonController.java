package project.yourNews.domains.common.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/unsubscribe")
    public String showUnsubscribePage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "unsubscribe"; // unsubscribe.html 템플릿을 반환
    }
}