package pl.thesis.projects_helper.interfaces;


public interface IUSOSService {
    String getAuthorizeUrl();

    void exchangeAndSaveAccessToken(String oauthVerifier);
    String getUserName();
}
