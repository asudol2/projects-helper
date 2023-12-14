package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.request.TopicConfirmRequest;
import pl.thesis.projects_helper.model.request.TopicRequest;
import pl.thesis.projects_helper.utils.TopicOperationResult;

import java.util.List;

public interface ITopicService {
    List<TopicEntity> getAllUserCurrentRelatedTopics(String token, String secret);

    List<TopicEntity> getAllCourseCurrentRelatedTopics(String courseID,
                                                       String token, String secret);

    TopicOperationResult addTopic(String token, String secret, TopicRequest topicRequest);

    List<TopicEntity> getSelectiveUserTopicsByCourse(String token, String secret, String courseID);

    TopicEntity getTopicById(String token, String secret, int topicId);

    boolean confirmTemporaryTopic(String token, String secret, TopicConfirmRequest topic);
}
