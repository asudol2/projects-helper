package pl.thesis.projects_helper.interfaces;


import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;

public interface IUSOSService {
    String getAuthorizeUrl();

    TokenResponse exchangeAndSaveAccessToken(String oauthVerifier);
    LoginResponse getUserData(String token, String secret);
}
