package pl.thesis.projects_helper.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pl.thesis.projects_helper.interfaces.IUSOSService;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;
import pl.thesis.projects_helper.model.response.UsosAuthUrlResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class USOSController {
    private final IUSOSService usosService;
    @Value("${app.frontendUrl}")
    private String frontendUrl;

    public USOSController(IUSOSService usosService) {
        this.usosService = usosService;
    }

    @GetMapping("/login")
    public UsosAuthUrlResponse login(HttpServletResponse response) throws IOException {
        String authorizeUrl = usosService.getAuthorizeUrl();
        return new UsosAuthUrlResponse(authorizeUrl);
    }

    @GetMapping("/callback")
    public RedirectView loginSuccessCallback(@RequestParam("oauth_token") String oauthToken,
                                             @RequestParam("oauth_verifier") String oauthVerifier) {
        TokenResponse tokenResponse = usosService.exchangeAndSaveAccessToken(oauthVerifier);
        return new RedirectView(frontendUrl+"home/"+tokenResponse.token()+"/"+tokenResponse.secret());
    }

    @GetMapping("/name")
    public LoginResponse displayUserData(@RequestParam String token, @RequestParam String secret){
        return usosService.getUserData(token, secret);
    }
}
