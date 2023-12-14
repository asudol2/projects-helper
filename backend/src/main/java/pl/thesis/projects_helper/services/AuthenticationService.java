package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.utils.RequiresAuthentication;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.thesis.projects_helper.utils.URLArgsUtils.generateArgsUrl;
import static pl.thesis.projects_helper.utils.URLArgsUtils.requestOnEndpoint;


@Service
@Aspect
@Component
public class AuthenticationService {

    @Value("${usos.baseUrl}")
    private String usosBaseUrl;

    @Value("${consumer.key}")
    private String consumerKey;

    @Value("${consumer.secret}")
    private String consumerSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;


    public AuthenticationService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.mapper = objectMapper;
    }

    private JsonNode requestUsersEndpoint(AuthorizationData authData, Map<String, List<String>> args) {
        String url = usosBaseUrl + "users/user" + "?" + generateArgsUrl(args);
        return requestOnEndpoint(authData, restTemplate, url, consumerKey, consumerSecret);
    }

    @Before("@annotation(requiresAuthentication)")
    public void authenticate(JoinPoint joinPoint, RequiresAuthentication requiresAuthentication) throws Exception {
        Object[] args = joinPoint.getArgs();
        AuthorizationData authData = (AuthorizationData) args[0];

        Map<String, List<String>> urlArgs = new HashMap<>();
        urlArgs.put("fields", List.of("id"));
        JsonNode usosJson = requestUsersEndpoint(authData, urlArgs);

        if (usosJson.isEmpty())
            throw new Exception("Invalid user");
    }
}