package pl.thesis.projects_helper.interfaces;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.util.Pair;
import pl.thesis.projects_helper.model.CourseEntity;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.request.TokenRequest;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public interface IUSOSService {
    String getAuthorizeUrl(String loginToken);

    void exchangeAndSaveAccessToken(String oauthVerifier, String loginToken);

    LoginResponse getUserData(String token, String secret);

    String generateLoginToken();

    void saveOAuthCredentials(String oAuthToken, String oAuthSecret);

    TokenResponse getOAuthCredentials(TokenRequest request);

    boolean revokeAccessToken(String token, String secret);

}
