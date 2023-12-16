package pl.thesis.projects_helper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.IProjectService;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.model.request.TeamConfirmRequest;
import pl.thesis.projects_helper.model.request.TeamRequest;
import pl.thesis.projects_helper.repository.TeamRequestRepository;
import pl.thesis.projects_helper.services.AuthorizationService;

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
    public boolean addTeamRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                  @RequestBody TeamRequest teamRequest) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.addProjectRequest(authData, teamRequest);
    }

    @GetMapping("/requests")
    public Map<TopicEntity, List<List<UserEntity>>> getCourseTeamRequests(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("course_id") String courseID) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.getCourseTeamRequestsLists(authData, courseID);
    }

    @GetMapping("/teams")
    public Map<TopicEntity, List<UserEntity>> getCourseTeams(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("course_id") String courseID) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.getCourseTeams(authData, courseID);
    }

    @PostMapping("/confirm")
    public boolean confirmProjectRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @RequestBody TeamConfirmRequest teamConfirmRequest) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.confirmProjectRequest(authData, teamConfirmRequest);
    }

    @GetMapping("/autoa_ssign")
    public boolean autoAssign(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                              @RequestParam("course_id") String courseID) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.naiveAutoAssignTeams(authData, courseID);
    }
}
