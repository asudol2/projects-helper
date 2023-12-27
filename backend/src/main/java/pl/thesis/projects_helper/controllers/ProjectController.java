package pl.thesis.projects_helper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.IProjectService;
import pl.thesis.projects_helper.model.request.TeamConfirmRequest;
import pl.thesis.projects_helper.model.request.TeamRequest;
import pl.thesis.projects_helper.model.response.TeamResponse;
import pl.thesis.projects_helper.services.AuthorizationService;
import pl.thesis.projects_helper.utils.TeamRequestValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    private final IProjectService projectService;
    private final AuthorizationService authorizationService;

    @Autowired
    public ProjectController(IProjectService projectService, AuthorizationService authorizationService) {
        this.projectService = projectService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/add_request")
    public TeamRequestValidationResult addTeamRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody TeamRequest teamRequest) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.addProjectRequest(authData, teamRequest);
    }

    @GetMapping("/course_requests")
    public List<TeamResponse> getCourseTeamRequests(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("course_id") String courseID) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.getCourseTeamRequestsLists(authData, courseID);
    }

    @GetMapping("/course_teams")
    public List<TeamResponse> getCourseTeams(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("course_id") String courseID) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.getCourseTeams(authData, courseID);
    }

    @GetMapping("/user_teams")
    public List<TeamResponse> getUserTeams(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.getUserTeams(authData);
    }

    @PostMapping("/confirm")
    public boolean confirmProjectRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @RequestBody TeamConfirmRequest teamConfirmRequest) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.confirmProjectRequest(authData, teamConfirmRequest);
    }

    @PostMapping("/auto_assign")
    public boolean autoAssign(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                              @RequestParam("course_id") String courseID) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.naiveAutoAssignTeams(authData, courseID);
    }

    @PostMapping("/reject")
    public boolean rejectTeamRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                     @RequestBody Long teamRequestID) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.rejectTeamRequest(authData, teamRequestID);
    }
    
    @GetMapping("/user_requests")
    public List<TeamResponse> getUserTeamRequests(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);

        return projectService.getUserTeamRequests(authData);
    }
}
