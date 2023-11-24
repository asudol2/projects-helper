package pl.thesis.projects_helper.interfaces;


import com.fasterxml.jackson.databind.JsonNode;
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
//    LoginResponse getUserData(String token, String secret);

    String generateLoginToken();
    void saveOAuthCredentials(String oAuthToken, String oAuthSecret);
    TokenResponse getOAuthCredentials(TokenRequest request);

    JsonNode requestUsersEndpoint(String logintoken,
                                                    String func,
                                                    Map<String, List<String>> args);

//    public JsonNode requestGroupsEndpoint(String logintoken,
//                                          String func,
//                                          Map<String, List<String>> args);

    List<TopicEntity> getAllUserCurrentRelatedTopics(String TEMP_loginToken);
    List<TopicEntity> getAllCourseCurrentRelatedTopics(String courseID,
                                                              String TEMP_loginToken);
    List<CourseEntity> getAllUserCurrentRelatedCourses(String TEMP_login);
    boolean addTopic(TopicEntity topic);
}
