package pl.thesis.projects_helper.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.ITopicService;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.services.AuthorizationService;

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
    public boolean addOrUpdateTopic(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                    @RequestBody TopicEntity topic) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return topicService.addTopic(topic);
    }

    @GetMapping("/get")
    public List<TopicEntity> getSelectiveUserTopics(String courseID){
        courseID = "103D-INxxx-ISP-FO";
        String token = "QjexkEv3h2mpxNafKLHY";
        String secret = "VnCQJQ58jyTq7ng9ncgEBY2fhQPd8fLJk9CHs36Z";
        return topicService.getSelectiveUserTopicsByCourse(courseID, token, secret);
    }
}
