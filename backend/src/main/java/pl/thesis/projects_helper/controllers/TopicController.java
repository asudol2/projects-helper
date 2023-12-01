package pl.thesis.projects_helper.controllers;

import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.ITopicService;
import pl.thesis.projects_helper.model.TopicEntity;

import java.util.List;

@RestController
@RequestMapping("/topics")
@CrossOrigin(origins = "http://localhost:3000")
public class TopicController {
    private final ITopicService topicService;

    public TopicController(ITopicService topicService) {
        this.topicService = topicService;
    }
    @GetMapping("")
    public List<TopicEntity> getUserTopics(@RequestParam String token, @RequestParam String secret,
                                           @RequestParam("course_id") String courseID) {
        return topicService.getAllCourseCurrentRelatedTopics(courseID, token, secret);
    }

    @PostMapping("/add")
    public boolean addOrUpdateTopic(@RequestBody TopicEntity topic) {
        return topicService.addTopic(topic);
    }
}