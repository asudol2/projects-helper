package pl.thesis.projects_helper.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.ITopicService;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.services.AuthorizationService;
import pl.thesis.projects_helper.utils.TopicOperationResult;

import java.util.List;

@RestController
@RequestMapping("/topics")
@CrossOrigin(origins = "http://localhost:3000")
public class TopicController {
    private final ITopicService topicService;
    private final AuthorizationService authorizationService;

    public TopicController(ITopicService topicService, AuthorizationService authorizationService) {
        this.topicService = topicService;
        this.authorizationService = authorizationService;
    }
    @GetMapping("")
    public List<TopicEntity> getUserTopics(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                           @RequestParam("course_id") String courseID) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return topicService.getAllCourseCurrentRelatedTopics(courseID, authData.token(), authData.secret());
    }

    @PostMapping("/add")
    public TopicOperationResult addOrUpdateTopic(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                 @RequestBody TopicEntity topic) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return topicService.addTopic(topic, authData.token(), authData.secret());
    }

    @GetMapping("/get")
    public List<TopicEntity> getSelectiveUserTopics(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                    @RequestParam("course_id") String courseID){
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return topicService.getSelectiveUserTopicsByCourse(courseID, authData.token(), authData.secret());
    }
}
