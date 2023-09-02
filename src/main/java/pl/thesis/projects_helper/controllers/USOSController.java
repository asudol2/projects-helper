package pl.thesis.projects_helper.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pl.thesis.projects_helper.interfaces.IUSOSService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
//@RequestMapping("/login")
public class USOSController {
    private final IUSOSService usosService;
    public USOSController(IUSOSService usosService) {
        this.usosService = usosService;
    }

    @GetMapping("/login")
    void login(HttpServletResponse response) throws IOException {
        String authorizeUrl = usosService.getAuthorizeUrl();
        response.sendRedirect((authorizeUrl));
    }

    @GetMapping("/callback")
    public RedirectView loginSuccessCallback(@RequestParam("oauth_token") String oauthToken,
                                             @RequestParam("oauth_verifier") String oauthVerifier) {
        usosService.exchangeAndSaveAccessToken(oauthVerifier);

        return new RedirectView("http://localhost:8080/name");

    }

    @GetMapping("/name")
    public String displayName(){
        return usosService.getUserName();
    }
}
