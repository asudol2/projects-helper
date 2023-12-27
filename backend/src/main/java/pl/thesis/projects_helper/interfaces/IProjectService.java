package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.request.TeamConfirmRequest;
import pl.thesis.projects_helper.model.request.TeamRequest;
import pl.thesis.projects_helper.model.response.TeamResponse;
import pl.thesis.projects_helper.model.response.UserResponse;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;
import pl.thesis.projects_helper.utils.TeamRequestValidationResult;

import java.util.List;
import java.util.Map;

public interface IProjectService {

    TeamRequestValidationResult addProjectRequest(AuthorizationData authData, TeamRequest teamReq);

    List<TeamResponse> getCourseTeamRequestsLists(AuthorizationData authData, String courseID);

    List<TeamResponse> getCourseTeams(AuthorizationData authData, String courseID);

    boolean confirmProjectRequest(AuthorizationData authData, TeamConfirmRequest teamConfirmRequest);

    boolean naiveAutoAssignTeams(AuthorizationData authData, String courseID);

    boolean rejectTeamRequest(AuthorizationData authData, Long teamRequestID);

    List<TeamResponse> getUserTeamRequests(AuthorizationData authData);

    List<TeamResponse> getUserTeams(AuthorizationData authData);
}
