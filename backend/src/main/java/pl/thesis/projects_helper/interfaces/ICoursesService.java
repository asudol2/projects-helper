package pl.thesis.projects_helper.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.util.Pair;
import pl.thesis.projects_helper.model.CourseEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

import java.util.List;
import java.util.Map;

public interface ICoursesService {
    List<CourseEntity> getCurrentStudentCourses(AuthorizationData authData);

    List<CourseEntity> getCurrentStaffCourses(AuthorizationData authData);

    List<CourseEntity> getAllUserCurrentRelatedCourses(AuthorizationData authData);

    Pair<Integer, Integer> getUserStatusPair(AuthorizationData authData);

    JsonNode requestGroupsEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args);

    List<Map<String, Object>> retrieveCurrentCoursesGroup(JsonNode usosJson);

    boolean isCurrStudent(AuthorizationData authData);

    boolean isCurrStaff(AuthorizationData authData);

    JsonNode requestUsersEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args);

    JsonNode requestTermsEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args);

    String retrieveCurrentTerm(AuthorizationData authData);

    List<UserEntity> retrieveCurrentCourseLecturers(AuthorizationData authData, String courseID);

    List<UserEntity> retrieveCurrentCourseParticipants(AuthorizationData authData, String courseID);
}
