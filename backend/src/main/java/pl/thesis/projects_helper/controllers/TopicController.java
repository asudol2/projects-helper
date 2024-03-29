package pl.thesis.projects_helper.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.ITopicService;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.request.TopicConfirmRequest;
import pl.thesis.projects_helper.model.request.TopicRequest;
import pl.thesis.projects_helper.services.AuthorizationService;
import pl.thesis.projects_helper.utils.TopicOperationResult;

import java.util.List;

@RestController
@RequestMapping("/topics")
@CrossOrigin(origins = {"http://localhost:3000", "http://13.93.65.60:3000", "http://13.93.65.60", "http://10.1.0.4:3000", "http://10.1.0.4"})
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
        return topicService.getSelectiveUserTopicsByCourse(authData, courseID);
    }

    @GetMapping("/{id}")
    public TopicEntity getTopicById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                    @PathVariable int id) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return topicService.getTopicById(authData, id);
    }

    @PostMapping("/add")
    public TopicOperationResult addOrUpdateTopic(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                 @RequestBody TopicRequest topic) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return topicService.addTopic(authData, topic);
    }

    @PostMapping("/confirm")
    public boolean decideTemporaryTopic(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                        @RequestBody TopicConfirmRequest topic) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return topicService.confirmTemporaryTopic(authData, topic);
    }
}
