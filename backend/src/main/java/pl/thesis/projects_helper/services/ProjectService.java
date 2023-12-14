package pl.thesis.projects_helper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.interfaces.IProjectService;
import pl.thesis.projects_helper.model.*;
import pl.thesis.projects_helper.model.request.TeamConfirmRequest;
import pl.thesis.projects_helper.model.request.TeamRequest;
import pl.thesis.projects_helper.repository.TeamRepository;
import pl.thesis.projects_helper.repository.TeamRequestRepository;
import pl.thesis.projects_helper.repository.TopicRepository;
import pl.thesis.projects_helper.repository.UserInTeamRepository;
import pl.thesis.projects_helper.utils.RequiresAuthentication;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

import java.util.*;

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

    @Autowired
    UserService userService;


    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public ProjectService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
    }

    @Override
    @RequiresAuthentication
    public boolean addProjectRequest(AuthorizationData authData, TeamRequest teamReq) {
        String term = coursesService.retrieveCurrentTerm(authData);
        Optional<TopicEntity> optTopic = topicRepository.findByCourseIDAndTermAndTitle(teamReq.courseID(),
                term, teamReq.title());
        if (optTopic.isEmpty())
            return false;
        if (optTopic.get().isTemporary())
            return false;

        Long topicID = optTopic.get().getId();
        TeamRequestEntity teamReqEntity = new TeamRequestEntity(topicID, "topic");
        TeamRequestEntity addedTeamReq = teamRequestRepository.save(teamReqEntity);
        for (String userID: teamReq.userIDs()) {
            UserInTeamEntity user = new UserInTeamEntity(addedTeamReq, userID);
            userInTeamRepository.save(user);
        }
        return true;
    }

    @Override
    @RequiresAuthentication
    public Map<TopicEntity, List<UserEntity>> getCourseTeamRequests(AuthorizationData authData, String courseID) {
        List<TeamRequestEntity> courseTeamRequests = teamRequestRepository.findAllByCourseID(courseID);
        Map<TopicEntity, List<UserEntity>> finalMap = new HashMap<>();

        for (TeamRequestEntity teamReq: courseTeamRequests) {
            List<UserEntity> users = new ArrayList<>();
            List<String> userIDs = userInTeamRepository.findUserIDsByTeamRequest(teamReq);
            for (String userID: userIDs) {
                UserEntity user = userService.getStudentById(authData, userID);
                if (user != null)
                    users.add(user);
            }
            TopicEntity topic = teamReq.getTopic();
            finalMap.put(topic, users);
        }
        return finalMap;
    }

    private void acceptProjectRequest(TopicEntity topic,
                                         List<UserInTeamEntity> relatedUITs) {
        TeamEntity addedTeam = teamRepository.save(new TeamEntity(topic.getId(), "topic"));
        for (UserInTeamEntity uit: relatedUITs) {
            uit.setTeam(addedTeam);
            uit.setTeamRequest(null);
            userInTeamRepository.save(uit);
        }
        teamRequestRepository.deleteById(relatedUITs.get(0).getId());
    }

    private void rejectProjectRequest(List<UserInTeamEntity> relatedUITs) {
        Long teamRequestID = relatedUITs.get(0).getTeamRequest().getId();
        userInTeamRepository.deleteAll(relatedUITs);
        teamRequestRepository.deleteById(teamRequestID);
    }

    @Override
    @RequiresAuthentication
    public boolean confirmProjectRequest(AuthorizationData authData, TeamConfirmRequest teamConfirmRequest) {
        String term = coursesService.retrieveCurrentTerm(authData);
        Optional<TopicEntity> topic = topicRepository.findByCourseIDAndTermAndTitle(teamConfirmRequest.courseID(),
                term, teamConfirmRequest.title());
        if (topic.isEmpty())
            return false;

        List<UserInTeamEntity> uitList = userInTeamRepository.findUserInTeamEntitiesByUserIDIsIn(
                teamConfirmRequest.userIDs());
        List<UserInTeamEntity> relatedUITs = new ArrayList<>();
        for (UserInTeamEntity uit: uitList) {
            if (uit.getTeamRequest() == null)
                continue;
            if (Objects.equals(uit.getTeamRequest().getTopic().getId(), topic.get().getId())) {
                relatedUITs.add(uit);
            }
        }

        if (teamConfirmRequest.confirm())
            acceptProjectRequest(topic.get(), relatedUITs);
        else rejectProjectRequest(relatedUITs);
        return true;
    }
}
