package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.interfaces.ITopicService;
import pl.thesis.projects_helper.model.CourseEntity;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.request.TopicRequest;
import pl.thesis.projects_helper.repository.TopicRepository;
import pl.thesis.projects_helper.utils.TopicOperationResult;

import java.util.*;


@Service
@PropertySource("classpath:constants.properties")
public class TopicService implements ITopicService {
    @Autowired
    TopicRepository topicRepository;
    private final ObjectMapper mapper;
    private final ICoursesService coursesService;

    @Autowired
    public TopicService(ICoursesService coursesService) {
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

    private boolean isAuthorizedToManipulateTopic(TopicEntity topic, String token, String secret){
        List<CourseEntity> relCourses = coursesService.getAllUserCurrentRelatedCourses(token, secret);
        for (CourseEntity course: relCourses){
            if (course.getCourseID().equals(topic.getCourseID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TopicEntity getTopicById(int topicId) {
        return topicRepository.findTopicById(topicId);
    }

    @Override
    public TopicOperationResult addTopic(TopicRequest topicRequest, String token, String secret) {
        int lecturerId = 99; //TODO fix, temporary solution
        boolean temporary = true; //TODO fix, temporary solution
        String term = "2023Z"; //TODO fix, temporary solution
        TopicEntity topic = new TopicEntity(
                topicRequest.courseId(),
                lecturerId,
                topicRequest.title(),
                topicRequest.description(),
                term,
                temporary,
                getUserID(token, secret)
        );
        if (!isAuthorizedToManipulateTopic(topic, token, secret)){
            return TopicOperationResult.UNAUTHORIZED;
        }

        String message = "";
        try {
            topicRepository.save(topic);
        } catch (Exception e) {
            message = e.getMessage();
        }

        if (message.isEmpty())
            return TopicOperationResult.SUCCESS;
        if (message.contains("unique_title_per_course_and_term"))
            return TopicOperationResult.UNIQUE_TITLE_PER_COURSE_AND_TERM;
        if (message.contains("course_id"))
            return TopicOperationResult.COURSE_ID;
        if (message.contains("lecturer_id"))
            return TopicOperationResult.LECTURER_ID;
        if (message.contains("term"))
            return TopicOperationResult.TERM;
        if (message.contains("title"))
            return TopicOperationResult.TITLE;
        return TopicOperationResult.SIZE;
    }

    private String getUserID(String token, String secret){
        Map<String, String> idMap = mapper.convertValue(coursesService.requestUsersEndpoint(token, secret, "user",
                        new HashMap<>() {{
                            put("fields", List.of("id"));
                        }}),
        Map.class);
        return idMap.get("id");
    }
    private List<TopicEntity> getSelectiveStudentTopicsByCourse(String courseID, String token, String secret){
        List<TopicEntity> topics = getAllCourseCurrentRelatedTopics(courseID, token, secret);
        String userID = getUserID(token, secret);
        String targetTerm = "2023Z"; // TODO: find and add term retrieve method

        topics.removeIf(topic ->
                !topic.getTerm().equals(targetTerm) ||
                        (topic.isTemporary() && !topic.getPropounderID().equals(userID))
        );
        return topics;
    }

    @Override
    public List<TopicEntity> getSelectiveUserTopicsByCourse(String courseID, String token, String secret){
        // TODO: what with situation when user is student and lecturer at the same time?
        if (coursesService.isCurrStudent(token, secret)){
            return getSelectiveStudentTopicsByCourse(courseID, token, secret);
        } else if (coursesService.isCurrStaff(token, secret)) {
             List<TopicEntity> topics = getAllCourseCurrentRelatedTopics(courseID, token, secret);
             topics.removeIf(topic -> !topic.getTerm().equals("2023Z")); //TODO: find and add term retrieve method
             return topics;
        } else {
            return null;
        }
    }
}
