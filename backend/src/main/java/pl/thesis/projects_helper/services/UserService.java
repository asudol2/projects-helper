package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.IUserService;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.utils.RequiresAuthentication;
import pl.thesis.projects_helper.utils.UserActivityStatus;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;
import pl.thesis.projects_helper.utils.UserType;

import java.util.*;

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

    private JsonNode requestUsersEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "users/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(authData, restTemplate, url, consumerKey, consumerSecret);
    }

    private UserEntity getUserById(AuthorizationData authData, String userID) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("user_id", List.of(userID));
        args.put("fields", List.of("first_name",
                "middle_names",
                "last_name",
                "sex",
                "student_status",
                "staff_status",
                "email"));
        Map<String, Object> userData = mapper.convertValue(requestUsersEndpoint(authData, "user", args),
                new TypeReference<Map<String, Object>>() {});
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
    @RequiresAuthentication
    public UserEntity getLecturerById(AuthorizationData authData, String lecturerID) {
        UserEntity user = getUserById(authData, lecturerID);
        if (user.getStaffStatus() != UserActivityStatus.ACTIVE.getCode()){
            return null;
        }
        return user;
    }

    @Override
    @RequiresAuthentication
    public UserEntity getStudentById(AuthorizationData authData, String studentID) {
        UserEntity user = getUserById(authData, studentID);
        if (user.getStudentStatus() != UserActivityStatus.ACTIVE.getCode()){
            return null;
        }
        return user;
    }

    @Override
    public Pair<Integer, Integer> getUserStatusPair(AuthorizationData authData) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>(Arrays.asList("student_status", "staff_status")));
        JsonNode usosJson = requestUsersEndpoint(authData, "user", args);
        return Pair.of(usosJson.get("student_status").asInt(), usosJson.get("staff_status").asInt());
    }

    public boolean isCurrStudent(AuthorizationData authData) {
        return getUserStatusPair(authData).getFirst() == UserActivityStatus.ACTIVE.getCode();
    }

    public boolean isCurrStaff(AuthorizationData authData) {
        return getUserStatusPair(authData).getSecond() == UserActivityStatus.ACTIVE.getCode();
    }

    @Override
    public UserType getUserType(AuthorizationData authData) {
        if (isCurrStaff(authData)) {
            return UserType.STAFF;
        }
        if (isCurrStudent(authData)) {
            return UserType.STUDENT;
        }
        return UserType.NONE;
    }
}