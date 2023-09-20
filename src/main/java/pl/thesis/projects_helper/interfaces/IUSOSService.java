package pl.thesis.projects_helper.interfaces;


import pl.thesis.projects_helper.model.response.LoginResponse;

public interface IUSOSService {
    String getAuthorizeUrl();

    void exchangeAndSaveAccessToken(String oauthVerifier);
    LoginResponse getUserData();
}
