package project.yourNews.domains.common.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/unsubscribe")
    public String showUnsubscribePage() {

        return "unsubscribe";
    }

}