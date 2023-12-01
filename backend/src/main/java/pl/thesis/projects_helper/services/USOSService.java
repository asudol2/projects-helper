package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.IUSOSService;
import pl.thesis.projects_helper.model.CourseEntity;
import pl.thesis.projects_helper.model.LoginTokenEntity;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.request.TokenRequest;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;
import pl.thesis.projects_helper.repository.TokenRepository;

import jakarta.annotation.PostConstruct;
import pl.thesis.projects_helper.repository.TopicRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.security.SecureRandom;

@Service
@PropertySource("classpath:constants.properties")
public class USOSService implements IUSOSService {
    @Value("${consumer.key}")
    private String consumerKey;
    @Value("${consumer.secret}")
    private String consumerSecret;
    private OAuthConsumer consumer;
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${usos.baseUrl}")
    private String usosBaseUrl;
    @Value("${app.baseUrl}")
    private String appBaseUrl;
    @Value("${app.loginTokenLength}")
    private int loginTokenLength;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TopicRepository topicRepository;

    private OAuth1Template oauthTemplate;
    private final RestTemplate restTemplate;
    private OAuthToken requestToken;



    @Autowired
    public USOSService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final Logger logger = LoggerFactory.getLogger(USOSService.class);

    @PostConstruct
    private void init() {
        final String requestTokenUrl = usosBaseUrl +"oauth/request_token";
        final String authorizeUrl = usosBaseUrl +"oauth/authorize";
        final String accessTokenUrl = usosBaseUrl +"oauth/access_token";
        consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        this.oauthTemplate = new OAuth1Template(
                this.consumerKey, this.consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        this.oauthTemplate.setRequestFactory(requestFactory);
    }

    @Override
    public String getAuthorizeUrl(String loginToken) {
        OAuth1Parameters params = new OAuth1Parameters();
        params.set("interactivity", "confirm_user");
        assignRequestToken(loginToken);
        tokenRepository.save(new LoginTokenEntity(loginToken));
        return this.oauthTemplate.buildAuthorizeUrl(requestToken.getValue(), params);
    }

    private void assignRequestToken(String loginToken) {
        OAuth1Parameters params = new OAuth1Parameters();
        params.set("scopes", "email|studies");
        String callbackUrl = appBaseUrl+"callback?login_token="+loginToken;
        requestToken = this.oauthTemplate.fetchRequestToken(callbackUrl, params);
    }

    @Override
    public void exchangeAndSaveAccessToken(String oauthVerifier, String loginToken) {
        AuthorizedRequestToken authReqToken = new AuthorizedRequestToken(requestToken, oauthVerifier);
        OAuthToken accessToken = oauthTemplate.exchangeForAccessToken(authReqToken, null);
        LoginTokenEntity entity = tokenRepository.findByLoginToken(loginToken);
        if (entity == null) {
            //TODO do something
            return;
        }
        entity.setOauthToken(accessToken.getValue());
        entity.setOauthSecret(accessToken.getSecret());
        tokenRepository.save(entity);
    }

    private String generateSignedUrl(String url, String token, String secret)
            throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(token, secret);
        return consumer.sign(url.replace("|", "%7c")).replace("%7c", "|");
    }

    public void saveOAuthCredentials(String oAuthToken, String oAuthSecret) {
        String loginToken = generateLoginToken();
        tokenRepository.save(new LoginTokenEntity(loginToken, oAuthToken, oAuthSecret));
    }
    public String generateLoginToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[loginTokenLength];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String generateArgsUrl(Map<String, List<String>> args){
        StringBuilder builder = new StringBuilder();
        for (String arg: args.keySet()){
            builder.append(arg);
            builder.append("=");
            builder.append(String.join("%7c", args.get(arg)));
            builder.append("&");
        }
        builder.deleteCharAt(builder.length() -1);
        return builder.toString();
    }

    private JsonNode requestOnEndpoint(String logintoken,
                                       String baseUrl){
        LoginTokenEntity tokenEnt = tokenRepository.findByLoginToken(logintoken);
        JsonNode jsonOutput = mapper.createObjectNode();
        try {
            String signedUrl = generateSignedUrl(baseUrl,
                    tokenEnt.getOauthToken(),
                    tokenEnt.getOauthSecret());
            consumer.setTokenWithSecret(tokenEnt.getOauthToken(), tokenEnt.getOauthSecret());
            ResponseEntity<String> response = restTemplate.exchange(
                    signedUrl, HttpMethod.GET, null, String.class);
            jsonOutput = mapper.readTree(response.getBody());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return jsonOutput;
    }
    public JsonNode requestUsersEndpoint(String logintoken,
                                                    String func,
                                                    Map<String, List<String>> args) {

        String url = usosBaseUrl + "users/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(logintoken, url);
    }

    private JsonNode requestGroupsEndpoint(String logintoken,
                                       String func,
                                       Map<String, List<String>> args) {
        String url = usosBaseUrl + "groups/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(logintoken, url);
    }

//    public LoginResponse getLoginResponse(String token,
//                                          String secret,
//                                          Vector<String> fields){
//        Map<String, String> response = requestUsersEndpoint(token, secret, "user", fields);
//        return new LoginResponse(response.values());
//    }

    public TokenResponse getOAuthCredentials(TokenRequest request) {
        LoginTokenEntity token = tokenRepository.findByLoginToken(request.loginToken());
        return new TokenResponse(token.getOauthToken(), token.getOauthSecret());
    }

    private String retrieveCurrentRealisationIDFromTerms(JsonNode usosJson){
        List<Map<String, String>> termsList = mapper.convertValue(usosJson.get("terms"), List.class);

        List<String> ids = new ArrayList<>();
        for (Map<String, String> term: termsList){
            ids.add(term.get("id"));
        }
        Collections.sort(ids);
        return ids.get(ids.size()-1);
    }

    private List<Map<String, Object>> retrieveCurrentCoursesGroup(JsonNode usosJson){
        Map<String, List<Map<String, Object>>> groupsMap = mapper.convertValue(usosJson.get("groups"), Map.class);
        String realisationID = retrieveCurrentRealisationIDFromTerms(usosJson);
        List<Map<String, Object>> group = groupsMap.get(realisationID);

        List<Map<String, Object>> finalGroup = new ArrayList<>();
        Set<String> courseIDsSet = new HashSet<>();
        // remove duplicates somehow coming from USOS
        for (Map<String, Object> course: group){
            if (!courseIDsSet.contains((String)course.get("course_id"))){
                finalGroup.add(course);
            }
            courseIDsSet.add((String)course.get("course_id"));
        }
        return finalGroup;
    }

    public List<TopicEntity> getAllUserCurrentRelatedTopics(String TEMP_loginToken){
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>());
        args.get("fields").add("course_id");
        JsonNode usosJson = requestGroupsEndpoint(TEMP_loginToken, "user", args);
        List<Map<String, Object>> currGroup = retrieveCurrentCoursesGroup(usosJson);

        List<String> courseIDs = new ArrayList<>();
        for (Map<String, Object> course: currGroup){
            courseIDs.add((String) course.get("course_id"));
        }

        List<TopicEntity> foundTopics = new ArrayList<>();
        for (String courseID: courseIDs){
            foundTopics.addAll(topicRepository.findAllByCourseID(courseID));
        }
        return foundTopics;
    }

    public List<TopicEntity> getAllCourseCurrentRelatedTopics(String courseID,
                                                              String TEMP_loginToken){
        List<TopicEntity> topics = getAllUserCurrentRelatedTopics(TEMP_loginToken);
        List<TopicEntity> courseTopics = new ArrayList<>();
        for (TopicEntity topic: topics){
            if (topic.getCourseID().equals(courseID)){
                courseTopics.add(topic);
            }
        }
        return courseTopics;
    }

    public List<CourseEntity> getAllUserCurrentRelatedCourses(String TEMP_login){
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>(
                Arrays.asList("course_id", "lecturers")
        ));
        JsonNode usosJson = requestGroupsEndpoint(TEMP_login, "user", args);
        List<Map<String, Object>> currGroup = retrieveCurrentCoursesGroup(usosJson);

        List<CourseEntity> courses = new ArrayList<>();
        for (Map<String, Object> course: currGroup){
            Map<String, String> names = (Map<String, String>) course.get("course_name");
            courses.add(new CourseEntity(
                    (String)course.get("course_id"),
                    (String)course.get("term_id"),
                    names.get("pl"),
                    names.get("en"),
                    (String)course.get("relationship_type")));
        }
        return courses;
    }

    public boolean addTopic(TopicEntity topic){
        boolean success = false;
        try {
            topicRepository.save(topic);
            success = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return success;
    }

    public Pair<Integer, Integer> getUserStatusPair(String TEMP_loginToken){
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>(
                Arrays.asList("student_status", "staff_status")
        ));
        JsonNode usosJson = requestUsersEndpoint(TEMP_loginToken, "user", args);
        // student, staff
        return Pair.of(usosJson.get("student_status").asInt(),
                usosJson.get("staff_status").asInt());
    }

    private boolean isCurrStudent(String TEMP_loginToken){
        return getUserStatusPair(TEMP_loginToken).getFirst() == 2;
    }

    private boolean isCurrStaff(String TEMP_loginToken){
        return getUserStatusPair(TEMP_loginToken).getSecond() == 2;
    }

    private List<CourseEntity> getCurrentStatusRelatedCourses(String role,
                                                              String TEMP_loginToken){
        if (role.equals("participant") && !isCurrStudent(TEMP_loginToken)){
            return new ArrayList<>();
        }
        else if (role.equals("lecturer") && !isCurrStaff(TEMP_loginToken)) {
            return new ArrayList<>();
        }

        List<CourseEntity> currCourses = new ArrayList<>();
        for (CourseEntity course: getAllUserCurrentRelatedCourses(TEMP_loginToken)){
            if (course.getRelationshipType().equals(role)){
                currCourses.add(course);
            }
        }
        return currCourses;
    }
    public List<CourseEntity> getCurrentStudentCourses(String TEMP_loginToken){
       return getCurrentStatusRelatedCourses("participant", TEMP_loginToken);
    }

    public List<CourseEntity> getCurrentStaffCourses(String TEMP_loginToken){
        return getCurrentStatusRelatedCourses("lecturer", TEMP_loginToken);
    }


}
