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
import pl.thesis.projects_helper.model.response.ParticipantResponse;
import pl.thesis.projects_helper.repository.TeamRepository;
import pl.thesis.projects_helper.repository.TeamRequestRepository;
import pl.thesis.projects_helper.repository.TopicRepository;
import pl.thesis.projects_helper.repository.UserInTeamRepository;
import pl.thesis.projects_helper.utils.RequiresAuthentication;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

import java.util.*;
import java.util.stream.Collectors;

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
        if (optTopic.get().getMinTeamCap() > teamReq.userIDs().size() ||
            optTopic.get().getMaxTeamCap() < teamReq.userIDs().size()) {
            return false;
        }

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
    public Map<TopicEntity, List<List<UserEntity>>> getCourseTeamRequestsLists(AuthorizationData authData,
                                                                               String courseID) {
        List<TeamRequestEntity> courseTeamRequests = teamRequestRepository.findByTopicCourseID(courseID);
        Map<TopicEntity, List<List<UserEntity>>> finalMap = new HashMap<>();

        for (TeamRequestEntity teamReq: courseTeamRequests) {
            List<UserEntity> users = new ArrayList<>();
            List<String> userIDs = userInTeamRepository.findUserIDsByTeamRequest(teamReq);
            for (String userID: userIDs) {
                UserEntity user = userService.getStudentById(authData, userID);
                if (user != null)
                    users.add(user);
            }
            TopicEntity topic = teamReq.getTopic();
            if (!finalMap.containsKey(topic))
                finalMap.put(topic, new ArrayList<>());
            finalMap.get(topic).add(users);
        }
        return finalMap;
    }

    @Override
    @RequiresAuthentication
    public Map<TopicEntity, List<UserEntity>> getCourseTeams(AuthorizationData authData, String courseID) {
        List<TeamEntity> courseTeams = teamRepository.findAllByTopicCourseID(courseID);
        Map<TopicEntity, List<UserEntity>> finalMap = new HashMap<>();

        for (TeamEntity team: courseTeams) {
            List<UserEntity> users = new ArrayList<>();
            List<String> userIDs = userInTeamRepository.findUserIDsByTeam(team);
            for (String userID: userIDs) {
                UserEntity user = userService.getStudentById(authData, userID);
                if (user != null)
                    users.add(user);
            }
            TopicEntity topic = team.getTopic();
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

    private boolean assertEnoughTopicsForCourse(String courseID, int participantsNum) {
        List<TopicEntity> topics = topicRepository.findAllByCourseID(courseID);
        int maxParticipants = topics.stream()
                .mapToInt(TopicEntity::getMaxTeamCap)
                .sum();
        return maxParticipants >= participantsNum;
    }

    public List<TeamRequestEntity> getTeamRequestsUniqueByTopic(String courseID) {
        List<TeamRequestEntity> teamRequests = teamRequestRepository.findByTopicCourseID(courseID);
        Set<TeamRequestEntity> uniqueTeamRequests = new TreeSet<>(Comparator.
                comparing(request -> request.getTopic().getId()));
        uniqueTeamRequests.addAll(teamRequests);
        return new ArrayList<>(uniqueTeamRequests);
    }

    public void removePersonalConflicts(Map<TopicEntity, List<String>> topicToUserIDsMap,
                                         List<String> assignedUserIDs) {
        List<TopicEntity> teamRequestsTopics = new ArrayList<>(topicToUserIDsMap.keySet());
        for (TopicEntity topicFromRequests: teamRequestsTopics) {
            topicToUserIDsMap.get(topicFromRequests).removeAll(assignedUserIDs);
            if (topicToUserIDsMap.get(topicFromRequests).size() < topicFromRequests.getMinTeamCap()) {
                topicToUserIDsMap.remove(topicFromRequests);
            } else {
                assignedUserIDs.addAll(topicToUserIDsMap.get(topicFromRequests));
            }
        }
    }

    public void assignMaxStudentsNumberToRandomCourseTopics(Map<TopicEntity, List<String>> topicToUserIDsMap,
                                                             List<ParticipantResponse> participants,
                                                             String courseID) {
        Random random = new Random();
        List<TopicEntity> freeTopics = topicRepository.findAllByCourseID(courseID);
        freeTopics.removeIf(TopicEntity::isTemporary);
        freeTopics.removeAll(topicToUserIDsMap.keySet());
        freeTopics.removeAll(teamRepository.findAllByTopicCourseID(courseID)
                .stream()
                .map(TeamEntity::getTopic)
                .toList());

        for (TopicEntity freeTopic: freeTopics) {
            topicToUserIDsMap.put(freeTopic, new ArrayList<>());
            for (int i = 0; i < freeTopic.getMaxTeamCap(); i++) {
                topicToUserIDsMap.get(freeTopic).add(participants.remove(random.nextInt(participants.size())).ID());
            }
        }
    }

    public Map<TopicEntity, List<String>> prepareLocalDataForAutoAssign(AuthorizationData authData, String courseID) {
        List<TeamRequestEntity> uniqueTeamRequests = getTeamRequestsUniqueByTopic(courseID); // finalMap
        Map<TopicEntity, List<String>> topicToUserIDsMap = new HashMap<>();
        for (TeamRequestEntity teamRequest: uniqueTeamRequests) {
            topicToUserIDsMap.put(teamRequest.getTopic(), userInTeamRepository.findUserIDsByTeamRequest(teamRequest));
        }
        List<String> assignedUserIDs = new ArrayList<>();
        removePersonalConflicts(topicToUserIDsMap, assignedUserIDs);

        List<TeamEntity> teams = teamRepository.findAllByTopicCourseID(courseID);
        for (TeamEntity team: teams) {
            assignedUserIDs.addAll(userInTeamRepository.findUserIDsByTeam(team));
        }

        List<ParticipantResponse> participants = coursesService.retrieveCurrentCourseParticipants(authData, courseID);
        participants.removeIf(participant -> assignedUserIDs.contains(participant.ID()));

        assignMaxStudentsNumberToRandomCourseTopics(topicToUserIDsMap, participants, courseID);
        return topicToUserIDsMap;
    }

    public void removePastDBTeamRequestsRecords(String courseID) {
        List<TeamRequestEntity> courseTeamRequests = teamRequestRepository.findByTopicCourseID(courseID);
        List<UserInTeamEntity> UITs = userInTeamRepository.findUserInTeamEntitiesByTeamRequestTopicCourseID(courseID);
        userInTeamRepository.deleteAll(UITs);
        teamRequestRepository.deleteAll(courseTeamRequests);
    }

    public void insertTeamsAndUITsFromMap(Map<TopicEntity, List<String>> topicToUserIDsMap) {
        for (TopicEntity topic: topicToUserIDsMap.keySet()) {
            TeamEntity team = teamRepository.save(new TeamEntity(topic.getId(), "topic"));
            for (String userID: topicToUserIDsMap.get(topic)) {
                userInTeamRepository.save(new UserInTeamEntity(team, userID));
            }
        }
    }

    public boolean naiveAutoAssignTeams(AuthorizationData authData, String courseID) {
        // map of final validated assignments including preferences from team requests
        Map<TopicEntity, List<String>> topicToUserIDsMap = prepareLocalDataForAutoAssign(authData, courseID);
        if (!topicToUserIDsMap.isEmpty()) {
            removePastDBTeamRequestsRecords(courseID);
            insertTeamsAndUITsFromMap(topicToUserIDsMap);
        }

        Set<String> allCourseUserIDs = coursesService.retrieveCurrentCourseParticipants(authData, courseID)
                .stream()
                .map(ParticipantResponse::ID)
                .collect(Collectors.toSet());
        Set<String> assignedUserIDs = topicToUserIDsMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        return allCourseUserIDs.equals(assignedUserIDs);
    }
}
