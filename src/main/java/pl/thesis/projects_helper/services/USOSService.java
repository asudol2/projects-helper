package pl.thesis.projects_helper.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.stereotype.Service;
import pl.thesis.projects_helper.interfaces.IUSOSService;

import javax.annotation.PostConstruct;

@Service
public class USOSService implements IUSOSService {
    @Value("${consumer.key}")
    private String consumerKey;

    @Value("${consumer.secret}")
    private String consumerSecret;

    private OAuth1Operations oauthOperations;

    @PostConstruct
    private void init() {
        final String requestTokenUrl = "https://apps.usos.pw.edu.pl/services/oauth/request_token";
        final String authorizeUrl = "https://apps.usos.pw.edu.pl/services/oauth/authorize";
        final String accessTokenUrl = "https://apps.usos.pw.edu.pl/services/oauth/access_token";
        this.oauthOperations = new OAuth1Template(
                this.consumerKey, this.consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
    }
    @Override
    public String getAuthorizeUrl() {
        OAuth1Parameters params = new OAuth1Parameters();
        params.set("interactivity", "confirm_user");
        return this.oauthOperations.buildAuthorizeUrl(getToken().getValue(), params);
    }

    private OAuthToken getToken() {
        OAuth1Parameters params = new OAuth1Parameters();
        params.set("scopes", "email|studies");
        return this.oauthOperations.fetchRequestToken("http://localhost:8080/callback", params);
    }

    @Override
    public OAuthToken getAccessToken(String oauthToken,
                                     String oauthSecret){
        return new OAuthToken(oauthToken, oauthSecret);
    }



}
