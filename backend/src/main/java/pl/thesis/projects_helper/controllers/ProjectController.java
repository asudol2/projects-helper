package pl.thesis.projects_helper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.interfaces.IProjectService;
import pl.thesis.projects_helper.interfaces.ITopicService;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.model.request.TopicRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    private final IProjectService projectService;
    private final ICoursesService coursesService;
    private final ITopicService topicService;

    @Autowired
    public ProjectController(IProjectService projectService, ICoursesService coursesService, ITopicService topicService) {
        this.projectService = projectService;
        this.coursesService = coursesService;
        this.topicService = topicService;
    }

    @GetMapping("/request")
    public boolean addTeamRequest() {
        String user1ID = "1158935";
        String user2ID = "1158741";
        TopicRequest topic = new TopicRequest("103D-INxxx-ISP-FO", 1012113,
                "project_8", "description_8");
        String token = "33HsHHCbCDsuU4wZufh2";
        String secret = "GZUwtFKJYNqxGgj2j7AePyS42J6g7GhAy3nuzq4H";
        return projectService.addProjectRequest(topic, List.of(user1ID, user2ID), token, secret);
    }

    @GetMapping("/requests")
    public Map<TopicEntity, List<UserEntity>> getCourseTeamRequests() {
        String course = "103D-INxxx-ISP-FO";
        String token = "EthsGQYCCnufwnwyuEzr";
        String secret = "7JMR9gVXPbSkuu8rPGM2BjL7CUaRrnRBC22kW9kp";
        return projectService.getCourseTeamRequests(course, token, secret);
    }

}
