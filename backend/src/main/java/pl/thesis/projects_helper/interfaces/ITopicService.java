package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.request.TopicRequest;

import java.util.List;

public interface ITopicService {
    List<TopicEntity> getAllUserCurrentRelatedTopics(String token, String secret);

    List<TopicEntity> getAllCourseCurrentRelatedTopics(String courseID,
                                                       String token, String secret);

    TopicEntity getTopicById(int topicId);

    boolean addTopic(TopicRequest topic);
}
