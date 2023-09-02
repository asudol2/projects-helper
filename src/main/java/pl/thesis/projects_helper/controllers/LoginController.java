package pl.thesis.projects_helper.controllers;

import org.springframework.social.oauth1.OAuthToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pl.thesis.projects_helper.interfaces.IUSOSService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
//@RequestMapping("/login")
public class LoginController {
    private final IUSOSService usosService;
    private OAuthToken accessToken;

    public LoginController(IUSOSService usosService) {
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
        accessToken = usosService.getAccessToken(oauthToken, oauthVerifier);

        return new RedirectView("https://www.google.com");

    }
}
