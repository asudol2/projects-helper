package pl.thesis.projects_helper.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.thesis.projects_helper.interfaces.IUSOSService;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final IUSOSService usosService;

    public LoginController(IUSOSService usosService) {
        this.usosService = usosService;
    }

    @GetMapping("")
    String login() {
        String url = usosService.init();
        return "URL: "+url;
//        return "redirect:"+url;
    }
}
