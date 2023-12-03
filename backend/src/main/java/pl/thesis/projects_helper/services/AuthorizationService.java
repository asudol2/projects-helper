package pl.thesis.projects_helper.services;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthorizationService {
    public AuthorizationData processAuthorizationHeader(String header) {
        String decodedCredentials = new String(Base64.getDecoder().decode(header));
        String[] credentials = decodedCredentials.split(":");
        String token = credentials[0];
        String secret = credentials.length > 1 ? credentials[1] : "";
        return new AuthorizationData(token, secret);
    }

    public record AuthorizationData(String token, String secret) {
    }

}
