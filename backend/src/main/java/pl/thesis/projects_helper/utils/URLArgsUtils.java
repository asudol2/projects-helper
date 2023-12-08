package pl.thesis.projects_helper.utils;

import com.fasterxml.jackson.databind.JsonNode;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

public class URLArgsUtils {
    private static final Logger logger = LoggerFactory.getLogger(URLArgsUtils.class);


    public static String generateArgsUrl(Map<String, List<String>> args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args.keySet()) {
            if (args.get(arg).isEmpty()) {
                continue;
            }
            builder.append(arg);
            builder.append("=");
            builder.append(String.join("%7c", args.get(arg)));
            builder.append("&");
        }
        if (builder.isEmpty()){
            return "";
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String generateSignedUrl(String url, String token, String secret,
                                           String consumerKey, String consumerSecret)
            throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(token, secret);
        return consumer.sign(url.replace("|", "%7c")).replace("%7c", "|");
    }

    public static JsonNode requestOnEndpoint(RestTemplate restTemplate, String token, String secret, String baseUrl,
                                             String consumerKey, String consumerSecret) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonOutput = mapper.createObjectNode();
        try {
            String signedUrl = generateSignedUrl(baseUrl, token, secret, consumerKey, consumerSecret);
            ResponseEntity<String> response = restTemplate.exchange(signedUrl, HttpMethod.GET, null, String.class);
            jsonOutput = mapper.readTree(response.getBody());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return jsonOutput;
    }
}
