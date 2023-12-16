package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.model.request.TeamConfirmRequest;
import pl.thesis.projects_helper.model.request.TeamRequest;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

import java.util.List;
import java.util.Map;

public interface IProjectService {

    boolean addProjectRequest(AuthorizationData authData, TeamRequest teamReq);

    Map<TopicEntity, List<List<UserEntity>>> getCourseTeamsLists(AuthorizationData authData, String courseID);

    boolean confirmProjectRequest(AuthorizationData authData, TeamConfirmRequest teamConfirmRequest);

    boolean naiveAutoAssignTeams(AuthorizationData authData, String courseID);
}
