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
import pl.thesis.projects_helper.model.request.TopicConfirmRequest;
import pl.thesis.projects_helper.model.request.TopicRequest;
import pl.thesis.projects_helper.repository.TopicRepository;
import pl.thesis.projects_helper.utils.RequiresAuthentication;
import pl.thesis.projects_helper.utils.TopicOperationResult;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

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
    public List<TopicEntity> getAllUserCurrentRelatedTopics(AuthorizationData authData) {
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>());
        args.get("fields").add("course_id");
        JsonNode usosJson = coursesService.requestGroupsEndpoint(authData, "user", args);
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
    public List<TopicEntity> getAllCourseCurrentRelatedTopics(String courseID, AuthorizationData authData) {
        List<TopicEntity> topics = getAllUserCurrentRelatedTopics(authData);
        List<TopicEntity> courseTopics = new ArrayList<>();
        for (TopicEntity topic : topics) {
            if (topic.getCourseID().equals(courseID)) {
                courseTopics.add(topic);
            }
        }
        return courseTopics;
    }

    public boolean isAuthorizedToManipulateTopic(TopicEntity topic, AuthorizationData authData){
        List<CourseEntity> relCourses = coursesService.getAllUserCurrentRelatedCourses(authData);
        for (CourseEntity course: relCourses){
            if (course.getCourseID().equals(topic.getCourseID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @RequiresAuthentication
    public TopicEntity getTopicById(AuthorizationData authData, int topicId) {
        return topicRepository.findTopicById(topicId);
    }

    private TopicOperationResult analyzeTopicDBMessage(String message){
        if (message.isEmpty())
            return TopicOperationResult.SUCCESS;
        if (message.contains("unique_title_per_course_and_term"))
            return TopicOperationResult.UNIQUE_TITLE_PER_COURSE_AND_TERM;
        if (message.contains("course_id"))
            return TopicOperationResult.COURSE_ID;
        if (message.contains("term"))
            return TopicOperationResult.TERM;
        if (message.contains("title"))
            return TopicOperationResult.TITLE;
        if (message.contains("lecturer_id"))
            return TopicOperationResult.LECTURER_ID;
        if (message.contains("min_team_cap"))
            return TopicOperationResult.MIN_TEAM_CAP;
        if (message.contains("max_team_cap"))
            return TopicOperationResult.MAX_TEAM_CAP;
        if (message.contains("id"))
            return TopicOperationResult.ID;
        return TopicOperationResult.SIZE;
    }

    private TopicOperationResult validateTopicRequest(TopicRequest topic) {
        if (topic.courseId().length() > 64)
            return TopicOperationResult.COURSE_ID_SIZE;
        if (topic.title().length() > 256)
            return TopicOperationResult.TITLE_SIZE;
        if (topic.description().length() > 8192)
            return TopicOperationResult.DESCRIPTION_SIZE;
        if (topic.minCap() > topic.maxCap())
            return TopicOperationResult.MIN_TEAM_CAP;
        return TopicOperationResult.SUCCESS;
    }

    public TopicEntity createTopicEntityFromTopicRequest(AuthorizationData authData, TopicRequest topicRequest) {
        boolean temporary = !coursesService.isCurrStaff(authData);
        String term = coursesService.retrieveCurrentTerm(authData);
        return new TopicEntity(
                topicRequest.courseId(),
                topicRequest.lecturerID(),
                topicRequest.title(),
                topicRequest.description(),
                term,
                temporary,
                getUserID(authData),
                topicRequest.minCap(),
                topicRequest.maxCap()
        );
    }

    @Override
    @RequiresAuthentication
    public TopicOperationResult addTopic(AuthorizationData authData, TopicRequest topicRequest) {
        TopicOperationResult basicValidationResult = validateTopicRequest(topicRequest);
        if (basicValidationResult.getCode() != 0)
            return basicValidationResult;
        TopicEntity topic = createTopicEntityFromTopicRequest(authData, topicRequest);
        if (!isAuthorizedToManipulateTopic(topic, authData)){
            return TopicOperationResult.UNAUTHORIZED;
        }

        String message = "";
        try {
            topicRepository.save(topic);
        } catch (Exception e) {
            message = e.getMessage();
        }
        return analyzeTopicDBMessage(message);
    }

    public String getUserID(AuthorizationData authData) {
        Map<String, String> idMap = mapper.convertValue(coursesService.requestUsersEndpoint(authData, "user",
                        new HashMap<>() {{
                            put("fields", List.of("id"));
                        }}),
        Map.class);
        return idMap.get("id");
    }
    public List<TopicEntity> getSelectiveStudentTopicsByCourse(AuthorizationData authData, String courseID){
        List<TopicEntity> topics = getAllCourseCurrentRelatedTopics(courseID, authData);
        String userID = getUserID(authData);
        String targetTerm = coursesService.retrieveCurrentTerm(authData);

        topics.removeIf(topic ->
                !topic.getTerm().equals(targetTerm) ||
                        (topic.isTemporary() && !topic.getPropounderID().equals(userID))
        );
        return topics;
    }

    public List<TopicEntity> getSelectiveLecturerTopicsByCourse(AuthorizationData authData, String courseID) {
        List<TopicEntity> topics = getAllCourseCurrentRelatedTopics(courseID, authData);
        String currentTerm = coursesService.retrieveCurrentTerm(authData);
        topics.removeIf(topic -> !topic.getTerm().equals(currentTerm));
        return topics;
    }

    @Override
    @RequiresAuthentication
    public List<TopicEntity> getSelectiveUserTopicsByCourse(AuthorizationData authData, String courseID) {
        // what with situation when user is student and lecturer at the same time?
        if (coursesService.isCurrStaff(authData)) {
             return getSelectiveLecturerTopicsByCourse(authData, courseID);
        } else if (coursesService.isCurrStudent(authData)) {
            return getSelectiveStudentTopicsByCourse(authData, courseID);
        } else {
            return null;
        }
    }

    @Override
    @RequiresAuthentication
    public boolean confirmTemporaryTopic(AuthorizationData authData, TopicConfirmRequest topic) {
        String term = coursesService.retrieveCurrentTerm(authData);
        Optional<TopicEntity> topicEntityOpt = topicRepository.findByCourseIDAndTermAndTitle(topic.courseId(),
                term, topic.title());
        if (topicEntityOpt.isEmpty())
            return false;
        if (!topic.confirm()) {
            if (!topicEntityOpt.get().isTemporary())
                return false;
            topicRepository.delete(topicEntityOpt.get());
            return true;
        }
        topicEntityOpt.get().setTemporary(false);
        topicRepository.save(topicEntityOpt.get());
        return true;
    }
}
