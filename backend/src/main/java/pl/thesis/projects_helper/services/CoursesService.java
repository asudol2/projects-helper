package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.model.CourseEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.utils.UserActivityStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            courses.add(new CourseEntity((String) course.get("course_id"), (String) course.get("term_id"),
                    names.get("pl"), names.get("en"), (String) course.get("relationship_type")));
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
        return getUserStatusPair(token, secret).getFirst() == UserActivityStatus.ACTIVE.getCode();
    }

    public boolean isCurrStaff(String token, String secret) {
        return getUserStatusPair(token, secret).getSecond() == UserActivityStatus.ACTIVE.getCode();
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

    @Override
    public JsonNode requestTermsEndpoint(String token, String secret, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "terms/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(restTemplate, token, secret, url, consumerKey, consumerSecret);
    }

    @Override
    public String retrieveCurrentTerm(String token, String secret){
        Map<String, List<String>> args = new HashMap<>();
        args.put("active_only", List.of("true"));
        JsonNode usosJson = requestTermsEndpoint(token, secret, "terms_index", args);
        List<Map<String, String>> usosMap = mapper.convertValue(usosJson, List.class);

        LocalDate currDate = LocalDate.now();
        String currYear = String.valueOf(LocalDate.now().getYear());
        List<Map<String, String>> realizations = usosMap.stream()
                .filter(map -> map.get("id").contains(currYear))
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Map<String, String> realizationMap: realizations){
            LocalDate endDate = LocalDate.parse(realizationMap.get("end_date"), formatter);
            LocalDate startDate = LocalDate.parse(realizationMap.get("start_date"), formatter);
            if (currDate.isAfter(startDate) && currDate.isBefore(endDate)){
                return realizationMap.get("id");
            }
        }
        return null;
    }

    public JsonNode requestCoursesEndpoint(String token, String secret, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "courses/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(restTemplate, token, secret, url, consumerKey, consumerSecret);
    }
    @Override
    public List<UserEntity> retrieveCurrentCourseLecturers(String courseID, String token, String secret){
        Map<String, List<String>> args = new HashMap<>();
        args.put("course_id", List.of(courseID));
        args.put("term_id", List.of(retrieveCurrentTerm(token, secret)));
        args.put("fields", List.of("lecturers"));

        JsonNode usosJson = requestCoursesEndpoint(token, secret, "course_edition", args);
        List<Map<String, String>> lecturersMap = mapper.convertValue(usosJson.get("lecturers"), List.class);

        return  mapUsosUsersMapsListToUserEntitiesList(lecturersMap);
    }

    @Override
    public List<UserEntity> retrieveCurrentCourseParticipants(String courseID, String token, String secret) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("course_id", List.of(courseID));
        args.put("term_id", List.of(retrieveCurrentTerm(token, secret)));
        args.put("fields", List.of("participants"));

        JsonNode usosJson = requestCoursesEndpoint(token, secret, "course_edition", args);
        List<Map<String, String>> participantsMapsList = mapper.convertValue(usosJson.get("participants"), List.class);
        return mapUsosUsersMapsListToUserEntitiesList(participantsMapsList);
    }

    private List<UserEntity> mapUsosUsersMapsListToUserEntitiesList(List<Map<String, String>> usersMapsList) {
        List<UserEntity> participantsList = new ArrayList<>();
        for (Map<String, String> partMap: usersMapsList){
            participantsList.add(new UserEntity(partMap.get("id"),
                    partMap.get("first_name"),
                    partMap.get("last_name")));
        }
        return participantsList;
    }
}
