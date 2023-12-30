package pl.thesis.projects_helper.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import pl.thesis.projects_helper.model.CourseEntity;
import pl.thesis.projects_helper.model.response.ParticipantResponse;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

import java.util.List;
import java.util.Map;

public interface ICoursesService {
    List<CourseEntity> getCurrentStudentCourses(AuthorizationData authData);

    List<CourseEntity> getCurrentStaffCourses(AuthorizationData authData);

    List<CourseEntity> getAllUserCurrentRelatedCourses(AuthorizationData authData);

    JsonNode requestGroupsEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args);

    List<Map<String, Object>> retrieveCurrentCoursesGroup(JsonNode usosJson);

    JsonNode requestUsersEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args);

    JsonNode requestTermsEndpoint(AuthorizationData authData, String func, Map<String, List<String>> args);

    String retrieveCurrentTerm(AuthorizationData authData);

    List<ParticipantResponse> retrieveCurrentCourseLecturers(AuthorizationData authData, String courseID);

    List<ParticipantResponse> retrieveCurrentCourseParticipants(AuthorizationData authData, String courseID);

    String getCourseNameById(AuthorizationData authData, String courseId);
}
