package pl.thesis.projects_helper.interfaces;


import pl.thesis.projects_helper.model.request.TokenRequest;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

public interface IUSOSService {
    String getAuthorizeUrl(String loginToken);

    void exchangeAndSaveAccessToken(String oauthVerifier, String loginToken, int failRecursionDepth)
            throws Exception;

    LoginResponse getUserData(AuthorizationData authData);

    String generateLoginToken();

    void saveOAuthCredentials(String oAuthToken, String oAuthSecret);

    TokenResponse getOAuthCredentials(TokenRequest request);

    boolean revokeAccessToken(AuthorizationData authData);

}
