package pl.thesis.projects_helper.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.http.HttpHeaders;
import pl.thesis.projects_helper.interfaces.IUSOSService;
import pl.thesis.projects_helper.model.request.TokenRequest;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;
import pl.thesis.projects_helper.model.response.UsosAuthUrlResponse;
import pl.thesis.projects_helper.services.AuthorizationService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://13.93.65.60:3000", "http://13.93.65.60", "http://10.1.0.4:3000", "http://10.1.0.4"})
public class USOSController {
    private final IUSOSService usosService;
    @Value("${app.frontendUrl}")
    private String frontendUrl;
    private final int failRecursionDepth;

    private final AuthorizationService authorizationService;

    @Autowired
    public USOSController(IUSOSService usosService, AuthorizationService authorizationService) {
        this.usosService = usosService;
        this.authorizationService = authorizationService;
        this.failRecursionDepth = 3;
    }

    @GetMapping("/login")
    public UsosAuthUrlResponse login() {
        String loginToken = usosService.generateLoginToken();
        String authorizeUrl = usosService.getAuthorizeUrl(loginToken);
        return new UsosAuthUrlResponse(authorizeUrl, loginToken);
    }

    @GetMapping("/callback")
    public RedirectView loginSuccessCallback(@RequestParam("oauth_verifier") String oauthVerifier,
                                             @RequestParam("login_token") String loginToken) throws Exception {
        usosService.exchangeAndSaveAccessToken(oauthVerifier, loginToken, failRecursionDepth);
        return new RedirectView(frontendUrl + "home");
    }

    @GetMapping("/name")
    public LoginResponse displayUserData(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return usosService.getUserData(authData);
    }


    @PostMapping("/oauthcredentials")
    public TokenResponse getOAuthCredentials(@NotNull @RequestBody TokenRequest token) {
        return usosService.getOAuthCredentials(token);
    }

    @PostMapping("/revoke")
    public boolean revokeAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return usosService.revokeAccessToken(authData);
    }
}
