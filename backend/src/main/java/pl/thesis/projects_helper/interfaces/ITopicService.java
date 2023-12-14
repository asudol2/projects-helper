package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.request.TopicConfirmRequest;
import pl.thesis.projects_helper.model.request.TopicRequest;
import pl.thesis.projects_helper.utils.TopicOperationResult;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

import java.util.List;

public interface ITopicService {
    List<TopicEntity> getAllUserCurrentRelatedTopics(AuthorizationData authData);

    List<TopicEntity> getAllCourseCurrentRelatedTopics(String courseID,
                                                       AuthorizationData authData);

    TopicOperationResult addTopic(AuthorizationData authData, TopicRequest topicRequest);

    List<TopicEntity> getSelectiveUserTopicsByCourse(AuthorizationData authData, String courseID);

    TopicEntity getTopicById(AuthorizationData authData, int topicId);

    boolean confirmTemporaryTopic(AuthorizationData authData, TopicConfirmRequest topic);
}
