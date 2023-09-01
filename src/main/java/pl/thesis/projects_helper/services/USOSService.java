package pl.thesis.projects_helper.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.thesis.projects_helper.interfaces.IUSOSService;

@Service
public class USOSService implements IUSOSService {
    @Value("${consumer.key}")
    String consumerKey;

    @Value("${consumer.secret}")
    String consumerSecret;

    @Override
    public String init() {
        final String requestTokenUrl = "https://apps.usos.pw.edu.pl/services/oauth/request_token";
        final String authorizeURL = "https://apps.usos.pw.edu.pl/services/oauth/authorize";
        final String accessTokenUrl = "https://apps.usos.pw.edu.pl/services/oauth/access_token";

        OAuth1Template oAuth1Template = new OAuth1Template(
                consumerKey, consumerSecret, requestTokenUrl, authorizeURL, accessTokenUrl);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        oAuth1Template.setRequestFactory(requestFactory);

        OAuthToken token = oAuth1Template.fetchRequestToken("http://google.pl", null);
        return oAuth1Template.buildAuthorizeUrl(token.getValue(), null);
    }



}
