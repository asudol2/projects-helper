package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.IUserService;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.utils.UserActivityStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.thesis.projects_helper.utils.URLArgsUtils.generateArgsUrl;
import static pl.thesis.projects_helper.utils.URLArgsUtils.requestOnEndpoint;

@Service
@PropertySource("classpath:constants.properties")
public class UserService implements IUserService {

    @Value("${usos.baseUrl}")
    private String usosBaseUrl;

    @Value("${consumer.key}")
    private String consumerKey;

    @Value("${consumer.secret}")
    private String consumerSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public UserService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
    }

    private JsonNode requestUsersEndpoint(String token, String secret, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "users/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(restTemplate, token, secret, url, consumerKey, consumerSecret);
    }

    private UserEntity getUserById(String userID, String token, String secret) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("user_id", List.of(userID));
        args.put("fields", List.of("first_name",
                "middle_names",
                "last_name",
                "sex",
                "student_status",
                "staff_status",
                "email"));
        Map<String, Object> userData = mapper.convertValue(requestUsersEndpoint(token, secret, "user", args), Map.class);
        return new UserEntity(userID,
                (String) userData.get("first_name"),
                (String) userData.get("middle_names"),
                (String) userData.get("last_name"),
                (String) userData.get("sex"),
                (Integer) userData.get("student_status"),
                (Integer) userData.get("staff_status"),
                (String) userData.get("email"));
    }

    @Override
    public UserEntity getLecturerById(String lecturerID, String token, String secret) {
        UserEntity user = getUserById(lecturerID, token, secret);
        if (user.getStaffStatus() != UserActivityStatus.ACTIVE.getCode()){
            return null;
        }
        return user;
    }

    @Override
    public UserEntity getStudentById(String studentID, String token, String secret) {
        UserEntity user = getUserById(studentID, token, secret);
        if (user.getStudentStatus() != UserActivityStatus.ACTIVE.getCode()){
            return null;
        }
        return user;
    }
}