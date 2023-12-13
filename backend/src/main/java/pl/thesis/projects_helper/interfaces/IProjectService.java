package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.TeamRequestEntity;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.model.request.TeamConfirmRequest;
import pl.thesis.projects_helper.model.request.TeamRequest;
import pl.thesis.projects_helper.model.request.TopicRequest;

import java.util.List;
import java.util.Map;

public interface IProjectService {

    boolean addProjectRequest(TeamRequest teamReq, String token, String secret);

    Map<TopicEntity, List<UserEntity>> getCourseTeamRequests(String courseID, String token, String secret);

    boolean confirmProjectRequest(TeamConfirmRequest teamConfirmRequest, String token, String secret);
}
