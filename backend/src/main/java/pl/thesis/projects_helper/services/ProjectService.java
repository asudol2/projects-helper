package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.IProjectService;
import pl.thesis.projects_helper.model.TeamRequestEntity;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.model.UserInTeamEntity;
import pl.thesis.projects_helper.model.request.TopicRequest;
import pl.thesis.projects_helper.repository.TeamRepository;
import pl.thesis.projects_helper.repository.TeamRequestRepository;
import pl.thesis.projects_helper.repository.TopicRepository;
import pl.thesis.projects_helper.repository.UserInTeamRepository;

import java.util.List;
import java.util.Optional;

@Service
@PropertySource("classpath:constants.properties")
public class ProjectService implements IProjectService {
    @Value("${usos.baseUrl}")
    private String usosBaseUrl;
    @Value("${consumer.key}")
    private String consumerKey;
    @Value("${consumer.secret}")
    private String consumerSecret;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    TeamRequestRepository teamRequestRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserInTeamRepository userInTeamRepository;

    @Autowired
    CoursesService coursesService;


    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public ProjectService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
    }

    @Override
    public boolean addProjectRequest(TopicRequest topic, List<String> teammatesIDs, String token, String secret) {
        String term = coursesService.retrieveCurrentTerm(token, secret);
        Optional<TopicEntity> optTopic = topicRepository.findByCourseIDAndTermAndTitle(topic.courseId(), term, topic.title());
        if (optTopic.isEmpty())
            return false;

        Long topicID = optTopic.get().getId();
        TeamRequestEntity teamReq = new TeamRequestEntity(topicID, "topic");
        TeamRequestEntity addedTeamReq = teamRequestRepository.save(teamReq);
        for (String userID: teammatesIDs) {
//            UserInTeamEntity.UserInTeamId id = new UserInTeamEntity.UserInTeamId();
//            id.setTeamRequestID(addedTeamReq.getId());
//            id.setUserID(userID);
//            UserInTeamEntity UITEntity = new UserInTeamEntity();
//            UITEntity.setId(id);
//            UITrEntity.setTeamRequest(addedTeamReq);
            UserInTeamEntity user = new UserInTeamEntity(userID, addedTeamReq.getId());
            userInTeamRepository.save(user);
        }
        return true;
    }
}
