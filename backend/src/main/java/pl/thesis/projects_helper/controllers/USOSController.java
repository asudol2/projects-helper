package pl.thesis.projects_helper.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pl.thesis.projects_helper.interfaces.IUSOSService;
import pl.thesis.projects_helper.model.request.TokenRequest;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;
import pl.thesis.projects_helper.model.response.UsosAuthUrlResponse;

import java.util.List;

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
    public UsosAuthUrlResponse login() {
        String loginToken = usosService.generateLoginToken();
        String authorizeUrl = usosService.getAuthorizeUrl(loginToken);
        return new UsosAuthUrlResponse(authorizeUrl, loginToken);
    }

    @GetMapping("/callback")
    public RedirectView loginSuccessCallback(@RequestParam("oauth_verifier") String oauthVerifier,
                                             @RequestParam("login_token") String loginToken) {
        usosService.exchangeAndSaveAccessToken(oauthVerifier, loginToken);
        return new RedirectView(frontendUrl + "home");
    }

    @GetMapping("/name")
    public LoginResponse displayUserData(@NotNull @RequestParam String token, @NotNull @RequestParam String secret) {
        return usosService.getUserData(token, secret);
    }

    @PostMapping("/oauthcredentials")
    public TokenResponse getOAuthCredentials(@NotNull @RequestBody TokenRequest token) {
        return usosService.getOAuthCredentials(token);
    }
}
