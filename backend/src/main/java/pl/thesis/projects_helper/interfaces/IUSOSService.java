package pl.thesis.projects_helper.interfaces;


import pl.thesis.projects_helper.model.request.TokenRequest;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;

public interface IUSOSService {
    String getAuthorizeUrl(String loginToken);

    void exchangeAndSaveAccessToken(String oauthVerifier, String loginToken);
    LoginResponse getUserData(String token, String secret);

    String generateLoginToken();
    void saveOAuthCredentials(String oAuthToken, String oAuthSecret);
    TokenResponse getOAuthCredentials(TokenRequest request);
}
