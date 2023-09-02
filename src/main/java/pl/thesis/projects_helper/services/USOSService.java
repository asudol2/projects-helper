package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.http.HttpParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.social.oauth1.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.thesis.projects_helper.interfaces.IUSOSService;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class USOSService implements IUSOSService {
    @Value("${consumer.key}")
    private String consumerKey;

    @Value("${consumer.secret}")
    private String consumerSecret;

    private OAuth1Template oauthTemplate;
    private RestTemplate restTemplate;

    private OAuthToken requestToken;
    private OAuthToken accessToken;

    public OAuthToken getRequestToken(){
        return this.requestToken;
    }

    @Autowired
    public USOSService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void init(){
        final String requestTokenUrl = "https://apps.usos.pw.edu.pl/services/oauth/request_token";
        final String authorizeUrl = "https://apps.usos.pw.edu.pl/services/oauth/authorize";
        final String accessTokenUrl = "https://apps.usos.pw.edu.pl/services/oauth/access_token";
        this.oauthTemplate = new OAuth1Template(
                this.consumerKey, this.consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
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
        requestToken = this.oauthTemplate.fetchRequestToken("http://localhost:8080/callback", params);
    }

    @Override
    public void exchangeAndSaveAccessToken(String oauthVerifier){
        AuthorizedRequestToken authReqToken = new AuthorizedRequestToken(requestToken, oauthVerifier);
        accessToken = oauthTemplate.exchangeForAccessToken(authReqToken, null);
    }

    private String generateSignedUrl(String url){
        OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(accessToken.getValue(), accessToken.getSecret());
        try {
            return consumer.sign(url);
        } catch (Exception e) {
            return "COŚ SIĘ ZEPSUŁO";
        }
    }
    public String getUserName() {
        String url = "https://apps.usos.pw.edu.pl/services/users/user";
        String signedUrl = generateSignedUrl(url);


        ResponseEntity<String> response = restTemplate.exchange(signedUrl,
                                                                HttpMethod.GET,
                                                                null,
                                                                String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(response.getBody(),
                    new TypeReference<Map<String, Object>>() {});
            String id = (String) jsonMap.get("id");
            String firstName = (String) jsonMap.get("first_name");
            String lastName = (String) jsonMap.get("last_name");

            return id + "  " + firstName + "  " + lastName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.getBody();
    }
}
