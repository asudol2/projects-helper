package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.interfaces.ITopicService;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.repository.TopicRepository;

import java.util.*;


@Service
@PropertySource("classpath:constants.properties")
public class TopicService implements ITopicService {
    @Autowired
    TopicRepository topicRepository;
    private final ObjectMapper mapper;
    private final ICoursesService coursesService;


    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(USOSService.class);

    @Autowired
    public TopicService(RestTemplate restTemplate, ICoursesService coursesService) {
        this.restTemplate = restTemplate;
        this.coursesService = coursesService;
        mapper = new ObjectMapper();
    }

    @Override
    public List<TopicEntity> getAllUserCurrentRelatedTopics(String token, String secret) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>());
        args.get("fields").add("course_id");
        JsonNode usosJson = coursesService.requestGroupsEndpoint(token, secret, "user", args);
        List<Map<String, Object>> currGroup = coursesService.retrieveCurrentCoursesGroup(usosJson);

        List<String> courseIDs = new ArrayList<>();
        for (Map<String, Object> course : currGroup) {
            courseIDs.add((String) course.get("course_id"));
        }

        List<TopicEntity> foundTopics = new ArrayList<>();
        for (String courseID : courseIDs) {
            foundTopics.addAll(topicRepository.findAllByCourseID(courseID));
        }
        return foundTopics;
    }

    @Override
    public List<TopicEntity> getAllCourseCurrentRelatedTopics(String courseID, String token, String secret) {
        List<TopicEntity> topics = getAllUserCurrentRelatedTopics(token, secret);
        List<TopicEntity> courseTopics = new ArrayList<>();
        for (TopicEntity topic : topics) {
            if (topic.getCourseID().equals(courseID)) {
                courseTopics.add(topic);
            }
        }
        return courseTopics;
    }

    @Override
    public boolean addTopic(TopicEntity topic) {
        boolean success = false;
        try {
            topicRepository.save(topic);
            success = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return success;
    }

    private List<TopicEntity> getSelectiveStudentTopicsByCourse(String courseID, String token, String secret){
        List<TopicEntity> topics = getAllCourseCurrentRelatedTopics(courseID, token, secret);
        topics.removeIf(TopicEntity::isTemporary);
        return topics;
    }

    @Override
    public List<TopicEntity> getSelectiveUserTopicsByCourse(String courseID, String token, String secret){
        if (coursesService.isCurrStudent(token, secret)){
            return getSelectiveStudentTopicsByCourse(courseID, token, secret);
        } else if (coursesService.isCurrStaff(token, secret)) {
            return getAllCourseCurrentRelatedTopics(courseID, token, secret);
        } else {
            return null;
        }
    }
}
