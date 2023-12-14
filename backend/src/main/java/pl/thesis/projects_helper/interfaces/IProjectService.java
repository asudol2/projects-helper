package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.model.request.TeamConfirmRequest;
import pl.thesis.projects_helper.model.request.TeamRequest;

import java.util.List;
import java.util.Map;

public interface IProjectService {

    boolean addProjectRequest(String token, String secret, TeamRequest teamReq);

    Map<TopicEntity, List<UserEntity>> getCourseTeamRequests(String token, String secret, String courseID);

    boolean confirmProjectRequest(String token, String secret, TeamConfirmRequest teamConfirmRequest);
}
