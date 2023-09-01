package pl.thesis.projects_helper.interfaces;

import org.springframework.social.oauth1.OAuthToken;

public interface IUSOSService {
    String getAuthorizeUrl();

    OAuthToken getAccessToken(String oauthToken,
                              String oauthSecret);
}
