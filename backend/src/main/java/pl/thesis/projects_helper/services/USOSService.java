package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.IUSOSService;
import pl.thesis.projects_helper.model.LoginTokenEntity;
import pl.thesis.projects_helper.model.request.TokenRequest;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;
import pl.thesis.projects_helper.repository.TokenRepository;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

import jakarta.annotation.PostConstruct;
import pl.thesis.projects_helper.utils.RequiresAuthentication;
import pl.thesis.projects_helper.utils.UserType;

import java.util.*;
import java.security.SecureRandom;

import static pl.thesis.projects_helper.utils.URLArgsUtils.generateSignedUrl;
import static pl.thesis.projects_helper.utils.URLArgsUtils.requestOnEndpoint;

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
    UserService userService;

    private OAuth1Template oauthTemplate;
    private final RestTemplate restTemplate;
    private OAuthToken requestToken;


    @Autowired
    public USOSService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void init() {
        final String requestTokenUrl = usosBaseUrl + "oauth/request_token";
        final String authorizeUrl = usosBaseUrl + "oauth/authorize";
        final String accessTokenUrl = usosBaseUrl + "oauth/access_token";
        consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        this.oauthTemplate = new OAuth1Template(this.consumerKey, this.consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        this.oauthTemplate.setRequestFactory(requestFactory);
    }

    @Override
    public String getAuthorizeUrl(String loginToken) {
        OAuth1Parameters params = new OAuth1Parameters();
        assignRequestToken(loginToken);
        tokenRepository.save(new LoginTokenEntity(loginToken));
        return this.oauthTemplate.buildAuthorizeUrl(requestToken.getValue(), params);
    }

    private void assignRequestToken(String loginToken) {
        OAuth1Parameters params = new OAuth1Parameters();
        params.set("scopes", "email|studies");
        String callbackUrl = appBaseUrl + "callback?login_token=" + loginToken;
        requestToken = this.oauthTemplate.fetchRequestToken(callbackUrl, params);
    }

    @Override
    public void exchangeAndSaveAccessToken(String oauthVerifier, String loginToken, int failRecursionDepth) {
        if (failRecursionDepth < 0) {
            throw new RuntimeException("External server error. Try again later");
        }
        AuthorizedRequestToken authReqToken = new AuthorizedRequestToken(requestToken, oauthVerifier);
        OAuthToken accessToken = oauthTemplate.exchangeForAccessToken(authReqToken, null);
        LoginTokenEntity entity = tokenRepository.findByLoginToken(loginToken);
        if (entity == null) {
            exchangeAndSaveAccessToken(oauthVerifier, loginToken, failRecursionDepth - 1);
        }
        entity.setOauthToken(accessToken.getValue());
        entity.setOauthSecret(accessToken.getSecret());
        tokenRepository.save(entity);
    }

    @Override
    @RequiresAuthentication
    public LoginResponse getUserData(AuthorizationData authData) {
        String fields = "id|first_name|last_name";
        String url = usosBaseUrl + "users/user?fields=" + fields;
        try {
            String signedUrl = generateSignedUrl(authData, url, consumerKey, consumerSecret);
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseEntity<String> response = restTemplate.exchange(signedUrl, HttpMethod.GET, null, String.class);
            Map<String, Object> jsonMap = objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });
            String ID = (String) jsonMap.get("id");
            String firstName = (String) jsonMap.get("first_name");
            String lastName = (String) jsonMap.get("last_name");
            UserType userType = userService.getUserType(authData);
            //TODO fix
//            firstName = "Jan";
//            lastName = "Kowalski";
            return new LoginResponse(ID, firstName, lastName, userType);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void saveOAuthCredentials(String oAuthToken, String oAuthSecret) {
        String loginToken = generateLoginToken();
        tokenRepository.save(new LoginTokenEntity(loginToken, oAuthToken, oAuthSecret));
    }

    @Override
    public String generateLoginToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[loginTokenLength];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public TokenResponse getOAuthCredentials(TokenRequest request) {
        LoginTokenEntity token = tokenRepository.findByLoginToken(request.loginToken());
        return new TokenResponse(token.getOauthToken(), token.getOauthSecret());
    }

    @Override
    public boolean revokeAccessToken(AuthorizationData authData) {
        String url = usosBaseUrl + "oauth/revoke_token";
        try {
            JsonNode usosJson = requestOnEndpoint(authData, restTemplate, url, consumerKey, consumerSecret);
            Map<String, Boolean> usosMap = mapper.convertValue(usosJson, Map.class);

            if (usosMap.isEmpty()){
                return false;
            }
            if (usosMap.containsKey("success")){
                return usosMap.get("success");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return false;
    }
}
