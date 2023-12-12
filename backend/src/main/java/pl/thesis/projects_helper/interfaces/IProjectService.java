package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.TeamRequestEntity;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.model.request.TopicRequest;

import java.util.List;
import java.util.Map;

public interface IProjectService {

    boolean addProjectRequest(TopicRequest topic, List<String> teammatesIDs, String token, String secret);

    Map<TopicEntity, List<UserEntity>> getCourseTeamRequests(String courseID, String token, String secret);
}
