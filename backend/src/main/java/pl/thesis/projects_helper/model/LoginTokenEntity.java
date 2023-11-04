package pl.thesis.projects_helper.model;

import jakarta.persistence.*;

@Entity
@Table(name = "oauth_tokens")
public class LoginTokenEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "logintoken")
    private String loginToken;

    @Column(name = "oauth1_token")
    private String oauthToken;

    @Column(name = "oauth1_secret")
    private String oauthSecret;

    public LoginTokenEntity(String loginToken, String oauthToken, String oauth1_secret) {
        this.loginToken = loginToken;
        this.oauthToken = oauthToken;
        this.oauthSecret = oauth1_secret;
    }

    public LoginTokenEntity(String loginToken) {
        this.loginToken = loginToken;
    }
    public LoginTokenEntity() {

    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauth_token) {
        this.oauthToken = oauth_token;
    }

    public String getOauthSecret() {
        return oauthSecret;
    }

    public void setOauthSecret(String oauth1_secret) {
        this.oauthSecret = oauth1_secret;
    }


}
