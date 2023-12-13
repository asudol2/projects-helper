package pl.thesis.projects_helper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.interfaces.IProjectService;
import pl.thesis.projects_helper.interfaces.ITopicService;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.model.request.TeamConfirmRequest;
import pl.thesis.projects_helper.model.request.TeamRequest;
import pl.thesis.projects_helper.services.AuthorizationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    private final IProjectService projectService;
    private final ICoursesService coursesService;
    private final ITopicService topicService;
    private final AuthorizationService authorizationService;

    @Autowired
    public ProjectController(IProjectService projectService, ICoursesService coursesService,
                             ITopicService topicService, AuthorizationService authorizationService) {
        this.projectService = projectService;
        this.coursesService = coursesService;
        this.topicService = topicService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/request")
    public boolean addTeamRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                  @RequestBody TeamRequest teamRequest) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.addProjectRequest(teamRequest, authData.token(), authData.secret());
    }

    @GetMapping("/requests")
    public Map<TopicEntity, List<UserEntity>> getCourseTeamRequests(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("course_id") String courseID) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.getCourseTeamRequests(courseID, authData.token(), authData.secret());
    }

    @PostMapping("/confirm")
    public boolean confirmProjectRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @RequestBody TeamConfirmRequest teamConfirmRequest) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return projectService.confirmProjectRequest(teamConfirmRequest, authData.token(), authData.secret());
    }
}
