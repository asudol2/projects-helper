package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.model.CourseEntity;

import java.util.*;

import static pl.thesis.projects_helper.utils.URLArgsUtils.*;

@Service
@PropertySource("classpath:constants.properties")
public class CoursesService implements ICoursesService {
    @Value("${usos.baseUrl}")
    private String usosBaseUrl;
    @Value("${consumer.key}")
    private String consumerKey;
    @Value("${consumer.secret}")
    private String consumerSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;


    @Autowired
    public CoursesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
    }

    @Override
    public List<CourseEntity> getCurrentStudentCourses(String token, String secret) {
        return getCurrentStatusRelatedCourses("participant", token, secret);
    }

    @Override
    public List<CourseEntity> getCurrentStaffCourses(String token, String secret) {
        return getCurrentStatusRelatedCourses("lecturer", token, secret);
    }

    private List<CourseEntity> getCurrentStatusRelatedCourses(String role, String token, String secret) {
        if (role.equals("participant") && !isCurrStudent(token, secret)) {
            return new ArrayList<>();
        } else if (role.equals("lecturer") && !isCurrStaff(token, secret)) {
            return new ArrayList<>();
        }

        List<CourseEntity> currCourses = new ArrayList<>();
        for (CourseEntity course : getAllUserCurrentRelatedCourses(token, secret)) {
            if (course.getRelationshipType().equals(role)) {
                currCourses.add(course);
            }
        }
        return currCourses;
    }

    @Override
    public List<CourseEntity> getAllUserCurrentRelatedCourses(String token, String secret) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>(Arrays.asList("course_id", "lecturers")));
        JsonNode usosJson = requestGroupsEndpoint(token, secret, "user", args);
        List<Map<String, Object>> currGroup = retrieveCurrentCoursesGroup(usosJson);

        List<CourseEntity> courses = new ArrayList<>();
        for (Map<String, Object> course : currGroup) {
            Map<String, String> names = (Map<String, String>) course.get("course_name");
            courses.add(new CourseEntity((String) course.get("course_id"), (String) course.get("term_id"), names.get("pl"), names.get("en"), (String) course.get("relationship_type")));
        }
        return courses;
    }

    public JsonNode requestUsersEndpoint(String token, String secret, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "users/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(restTemplate, token, secret, url, consumerKey, consumerSecret);
    }

    @Override
    public Pair<Integer, Integer> getUserStatusPair(String token, String secret) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>(Arrays.asList("student_status", "staff_status")));
        JsonNode usosJson = requestUsersEndpoint(token, secret, "user", args);
        // student, staff
        return Pair.of(usosJson.get("student_status").asInt(), usosJson.get("staff_status").asInt());
    }

    public boolean isCurrStudent(String token, String secret) {
        return getUserStatusPair(token, secret).getFirst() == 2;  //TODO explain magic number, why 2?
    }

    public boolean isCurrStaff(String token, String secret) {
        return getUserStatusPair(token, secret).getSecond() == 2;  //TODO explain magic number, why 2?
    }

    @Override
    public JsonNode requestGroupsEndpoint(String token, String secret, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "groups/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(restTemplate, token, secret, url, consumerKey, consumerSecret);
    }

    @Override
    public List<Map<String, Object>> retrieveCurrentCoursesGroup(JsonNode usosJson) {
        Map<String, List<Map<String, Object>>> groupsMap = mapper.convertValue(usosJson.get("groups"), Map.class);
        String realisationID = retrieveCurrentRealisationIDFromTerms(usosJson);
        List<Map<String, Object>> group = groupsMap.get(realisationID);

        List<Map<String, Object>> finalGroup = new ArrayList<>();
        Set<String> courseIDsSet = new HashSet<>();
        // remove duplicates somehow coming from USOS
        for (Map<String, Object> course : group) {
            if (!courseIDsSet.contains((String) course.get("course_id"))) {
                finalGroup.add(course);
            }
            courseIDsSet.add((String) course.get("course_id"));
        }
        return finalGroup;
    }

    private String retrieveCurrentRealisationIDFromTerms(JsonNode usosJson) {
        List<Map<String, String>> termsList = mapper.convertValue(usosJson.get("terms"), List.class);

        List<String> ids = new ArrayList<>();
        for (Map<String, String> term : termsList) {
            ids.add(term.get("id"));
        }
        Collections.sort(ids);
        return ids.get(ids.size() - 1);
    }
}
