package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.IUSOSService;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@PropertySource("classpath:constants.properties")
public class USOSService implements IUSOSService {
    @Value("${consumer.key}")
    private String consumerKey;
    @Value("${consumer.secret}")
    private String consumerSecret;
    @Value("${usos.baseUrl}")
    private String usosBaseUrl;
    @Value("${app.baseUrl}")
    private String appBaseUrl;
    @Value("${app.frontendUrl}")
    private String frontendUrl;

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
        this.oauthTemplate = new OAuth1Template(
                this.consumerKey, this.consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        this.oauthTemplate.setRequestFactory(requestFactory);
    }

    @Override
    public String getAuthorizeUrl() {
        OAuth1Parameters params = new OAuth1Parameters();
        params.set("interactivity", "confirm_user");
        assignRequestToken();
        return this.oauthTemplate.buildAuthorizeUrl(requestToken.getValue(), params);
    }

    private void assignRequestToken() {
        OAuth1Parameters params = new OAuth1Parameters();
        params.set("scopes", "email|studies");
        requestToken = this.oauthTemplate.fetchRequestToken(appBaseUrl+"callback", params);
    }

    @Override
    public TokenResponse exchangeAndSaveAccessToken(String oauthVerifier) {
        AuthorizedRequestToken authReqToken = new AuthorizedRequestToken(requestToken, oauthVerifier);
        OAuthToken accessToken = oauthTemplate.exchangeForAccessToken(authReqToken, null);
        return new TokenResponse(accessToken.getValue(), accessToken.getSecret());
    }

    private String generateSignedUrl(String url, String token, String secret)
            throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(token, secret);
        return consumer.sign(url.replace("|", "%7c")).replace("%7c", "|");
    }

    public LoginResponse getUserData(String token, String secret) {
        String fields = "id|first_name|last_name|email|student_number";
        String url = usosBaseUrl+"users/user?fields=" + fields;
        try {
            String signedUrl = generateSignedUrl(url, token, secret);
            ObjectMapper objectMapper = new ObjectMapper();
            OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
            consumer.setTokenWithSecret(token, secret);
            ResponseEntity<String> response = restTemplate.exchange(
                                                    signedUrl, HttpMethod.GET, null, String.class);
            Map<String, Object> jsonMap = objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });
            String id = (String) jsonMap.get("id");
            String firstName = (String) jsonMap.get("first_name");
            String lastName = (String) jsonMap.get("last_name");
            String email = (String) jsonMap.get("email");
            String studentNumber = (String) jsonMap.get("student_number");
            return new LoginResponse(id, firstName, lastName, email, studentNumber);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
