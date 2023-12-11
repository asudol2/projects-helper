package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.model.request.TopicRequest;

import java.util.List;

public interface IProjectService {

    boolean addProjectRequest(TopicRequest topic, List<String> teammatesIDs, String token, String secret);
}
