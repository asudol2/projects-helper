package pl.thesis.projects_helper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.interfaces.IProjectService;
import pl.thesis.projects_helper.interfaces.ITopicService;
import pl.thesis.projects_helper.model.request.TopicRequest;

import java.util.List;

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
        String token = "Eqeje7DV4fhxLzdgesZL";
        String secret = "CvnDHvkQ5h4LSZSs3Bsz42tebYZQYmWj35V8Sj95";
        return projectService.addProjectRequest(topic, List.of(user1ID, user2ID), token, secret);
    }
}
