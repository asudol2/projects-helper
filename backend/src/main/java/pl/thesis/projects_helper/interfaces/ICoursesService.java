package pl.thesis.projects_helper.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.util.Pair;
import pl.thesis.projects_helper.model.CourseEntity;

import java.util.List;
import java.util.Map;

public interface ICoursesService {
    List<CourseEntity> getCurrentStudentCourses(String token, String secret);

    List<CourseEntity> getCurrentStaffCourses(String token, String secret);

    List<CourseEntity> getAllUserCurrentRelatedCourses(String token, String secret);

    Pair<Integer, Integer> getUserStatusPair(String token, String secret);

    JsonNode requestGroupsEndpoint(String token, String secret, String func, Map<String, List<String>> args);

    List<Map<String, Object>> retrieveCurrentCoursesGroup(JsonNode usosJson);

    boolean isCurrStudent(String token, String secret);

    boolean isCurrStaff(String token, String secret);

    JsonNode requestUsersEndpoint(String token, String secret, String func, Map<String, List<String>> args);
}
