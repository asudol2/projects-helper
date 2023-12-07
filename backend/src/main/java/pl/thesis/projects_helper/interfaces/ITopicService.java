package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.TopicEntity;

import java.util.List;

public interface ITopicService {
    List<TopicEntity> getAllUserCurrentRelatedTopics(String token, String secret);

    List<TopicEntity> getAllCourseCurrentRelatedTopics(String courseID,
                                                       String token, String secret);

    TopicEntity getTopicById(int topicId);

    boolean addTopic(TopicEntity topic);
}
