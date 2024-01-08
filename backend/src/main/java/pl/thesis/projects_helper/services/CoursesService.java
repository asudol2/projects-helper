package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.model.CourseEntity;
import pl.thesis.projects_helper.model.CourseName;
import pl.thesis.projects_helper.model.response.ParticipantResponse;
import pl.thesis.projects_helper.utils.RequiresAuthentication;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

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
    UserService userService;


    @Autowired
    public CoursesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
    }

    @Override
    @RequiresAuthentication
    public List<CourseEntity> getCurrentStudentCourses(AuthorizationData authData) {
        return getCurrentStatusRelatedCourses(authData, "participant");
    }

    @Override
    @RequiresAuthentication
    public List<CourseEntity> getCurrentStaffCourses(AuthorizationData authData) {
        return getCurrentStatusRelatedCourses(authData, "lecturer");
    }

    private List<CourseEntity> getCurrentStatusRelatedCourses(AuthorizationData authData, String role) {
        if (role.equals("participant") && !userService.isCurrStudent(authData)) {
            return new ArrayList<>();
        }
        if (role.equals("lecturer") && !userService.isCurrStaff(authData)) {
            return new ArrayList<>();
        }

        List<CourseEntity> currCourses = new ArrayList<>();
        for (CourseEntity course : getAllUserCurrentRelatedCourses(authData)) {
            if (course.getRelationshipType().equals(role)) {
                currCourses.add(course);
            }
        }
        return currCourses;
    }

    @Override
    @RequiresAuthentication
    public List<CourseEntity> getAllUserCurrentRelatedCourses(AuthorizationData authData) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>(Arrays.asList("course_id", "lecturers")));
        JsonNode usosJson = requestGroupsEndpoint(authData, "user", args);
        List<Map<String, Object>> currGroup = retrieveCurrentCoursesGroup(usosJson);

        List<CourseEntity> courses = new ArrayList<>();
        for (Map<String, Object> course : currGroup) {
            Map<String, String> names = (Map<String, String>) course.get("course_name");
            courses.add(new CourseEntity((String) course.get("course_id"), (String) course.get("term_id"),
                    names.get("pl"), names.get("en"), (String) course.get("relationship_type")));
        }
        return courses;
    }

    public JsonNode requestUsersEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "users/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(authData, restTemplate, url, consumerKey, consumerSecret);
    }

    @Override
    public JsonNode requestGroupsEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "groups/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(authData, restTemplate, url, consumerKey, consumerSecret);
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
    public JsonNode requestTermsEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "terms/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(authData, restTemplate, url, consumerKey, consumerSecret);
    }

    @Override
    @RequiresAuthentication
    public String retrieveCurrentTerm(AuthorizationData authData){
        Map<String, List<String>> args = new HashMap<>();
        args.put("active_only", List.of("true"));
        JsonNode usosJson = requestTermsEndpoint(authData, "terms_index", args);
        List<Map<String, String>> usosMap = mapper.convertValue(usosJson, List.class);

        LocalDate currDate = LocalDate.now();
        String currYear = String.valueOf(LocalDate.now().getYear());
        String prevYear = String.valueOf(currDate.minusYears(1).getYear());
        List<Map<String, String>> realizations = usosMap.stream()
                .filter(map -> {
                    String id = map.get("id");
                    return id.contains(currYear) || id.contains(prevYear);
                })
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

    public JsonNode requestCoursesEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args) {
        String url = usosBaseUrl + "courses/" + func + "?" + generateArgsUrl(args);
        return requestOnEndpoint(authData, restTemplate, url, consumerKey, consumerSecret);
    }
    @Override
    @RequiresAuthentication
    public List<ParticipantResponse> retrieveCurrentCourseLecturers(AuthorizationData authData,
                                                                    String courseID) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("course_id", List.of(courseID));
        args.put("term_id", List.of(retrieveCurrentTerm(authData)));
        args.put("fields", List.of("lecturers"));

        JsonNode usosJson = requestCoursesEndpoint(authData, "course_edition", args);
        List<Map<String, String>> lecturersMap = mapper.convertValue(usosJson.get("lecturers"), List.class);

        return mapUsosUsersMapsListToCourseParticipantResponseList(lecturersMap);
    }

    @Override
    @RequiresAuthentication
    public List<ParticipantResponse> retrieveCurrentCourseParticipants(AuthorizationData authData,
                                                                       String courseID) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("course_id", List.of(courseID));
        args.put("term_id", List.of(retrieveCurrentTerm(authData)));
        args.put("fields", List.of("participants"));

        JsonNode usosJson = requestCoursesEndpoint(authData, "course_edition", args);
        List<Map<String, String>> participantsMapsList = mapper.convertValue(usosJson.get("participants"), List.class);
        return mapUsosUsersMapsListToCourseParticipantResponseList(participantsMapsList);
    }

    @Override
    @RequiresAuthentication
    public String getCourseNameById(AuthorizationData authData, String courseId) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("course_id", List.of(courseId));
        args.put("fields", List.of("name"));
        JsonNode usosJson = requestCoursesEndpoint(authData, "course", args);
        return mapper.convertValue(usosJson.get("name"), CourseName.class).pl();
    }

    private List<ParticipantResponse> mapUsosUsersMapsListToCourseParticipantResponseList(
            List<Map<String, String>> usersMapsList) {
        List<ParticipantResponse> participantsList = new ArrayList<>();
        for (Map<String, String> partMap: usersMapsList){
            participantsList.add(new ParticipantResponse(
                    partMap.get("id"),
                    partMap.get("first_name"),
                    partMap.get("last_name")));
        }
        return participantsList;
    }
}
