package pl.thesis.projects_helper.model.response;

public class UsosAuthUrlResponse {
    String usosURL;
    String loginToken;

    public UsosAuthUrlResponse(String url, String loginToken) {
        this.usosURL = url;
        this.loginToken = loginToken;
    }

    public String getUsosURL() {
        return usosURL;
    }

    public String getLoginToken() {
        return loginToken;
    }
}
