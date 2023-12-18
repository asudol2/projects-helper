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
import pl.thesis.projects_helper.model.response.UserResponse;
import pl.thesis.projects_helper.repository.TeamRepository;
import pl.thesis.projects_helper.repository.TeamRequestRepository;
import pl.thesis.projects_helper.repository.TopicRepository;
import pl.thesis.projects_helper.repository.UserInTeamRepository;
import pl.thesis.projects_helper.utils.RequiresAuthentication;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;
import pl.thesis.projects_helper.utils.TeamRequestValidationResult;

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

    @Autowired
    USOSService usosService;


    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public ProjectService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
    }

    private boolean sameTeamRequestExists(TeamRequest teamReq, String term) {
        Optional<TopicEntity> optTopic = topicRepository.findByCourseIDAndTermAndTitle(teamReq.courseID(),
                term, teamReq.title());
        if (optTopic.isEmpty())
            return false;

        List<String> userIDs = userInTeamRepository.findUserIDsByTeamRequestTopicID(optTopic.get().getId());
        List<UserInTeamEntity> UITs = userInTeamRepository.findUserInTeamEntitiesByUserIDIsIn(userIDs);
        UITs.removeIf(uit -> uit.getTeamRequest() == null);
        UITs.removeIf(uit -> uit.getTeamRequest().getTopic() != optTopic.get());
        return UITs.size() == teamReq.userIDs().size();
    }

    private TeamRequestValidationResult validateTeamRequest(AuthorizationData authData, TeamRequest teamReq) {
        if (teamReq.userIDs().size() != new HashSet<>(teamReq.userIDs()).size())
            return TeamRequestValidationResult.NONUNIQUE;
        String term = coursesService.retrieveCurrentTerm(authData);
        Optional<TopicEntity> optTopic = topicRepository.findByCourseIDAndTermAndTitle(teamReq.courseID(),
                term, teamReq.title());
        if (optTopic.isEmpty())
            return TeamRequestValidationResult.NO_TOPIC;
        if (optTopic.get().isTemporary())
            return TeamRequestValidationResult.TEMP_TOPIC;
        if (optTopic.get().getMinTeamCap() > teamReq.userIDs().size() ||
                optTopic.get().getMaxTeamCap() < teamReq.userIDs().size()) {
            return TeamRequestValidationResult.SIZE_ERR;
        }
        if (sameTeamRequestExists(teamReq, term))
            return TeamRequestValidationResult.SAME_TEAM_REQ;
        return TeamRequestValidationResult.SUCCESS;
    }

    @Override
    @RequiresAuthentication
    public TeamRequestValidationResult addProjectRequest(AuthorizationData authData, TeamRequest teamReq) {
        List<String> updatedUserIDs = new ArrayList<>(teamReq.userIDs());
        updatedUserIDs.add((usosService.getUserData(authData).ID()));
        TeamRequest updatedTeamRequest = new TeamRequest(
                teamReq.courseID(),
                teamReq.title(),
                updatedUserIDs
        );
        TeamRequestValidationResult validationResult = validateTeamRequest(authData, teamReq);
        if (validationResult != TeamRequestValidationResult.SUCCESS)
            return validationResult;

        Long topicID = topicRepository.findByCourseIDAndTermAndTitle(
                teamReq.courseID(),
                coursesService.retrieveCurrentTerm(authData),
                teamReq.title()).get().getId();
        TeamRequestEntity teamReqEntity = new TeamRequestEntity(topicID, "topic");
        TeamRequestEntity addedTeamReq = teamRequestRepository.save(teamReqEntity);
        for (String userID: updatedTeamRequest.userIDs()) {
            UserInTeamEntity user = new UserInTeamEntity(addedTeamReq, userID);
            userInTeamRepository.save(user);
        }
        return TeamRequestValidationResult.SUCCESS;
    }

    @Override
    @RequiresAuthentication
    public Map<Long, List<List<UserResponse>>> getCourseTeamRequestsLists(AuthorizationData authData,
                                                                               String courseID) {
        List<TeamRequestEntity> courseTeamRequests = teamRequestRepository.findByTopicCourseID(courseID);
        Map<Long, List<List<UserResponse>>> finalMap = new HashMap<>();

        for (TeamRequestEntity teamReq: courseTeamRequests) {
            List<UserResponse> users = new ArrayList<>();
            List<String> userIDs = userInTeamRepository.findUserIDsByTeamRequest(teamReq);
            for (String userID: userIDs) {
                UserEntity user = userService.getStudentById(authData, userID);
                if (user != null)
                    users.add(new UserResponse(
                            user.getID(),
                            user.getFirstName(),
                            user.getMiddleNames(),
                            user.getLastName()
                    ));
            }
            TopicEntity topic = teamReq.getTopic();
            if (!finalMap.containsKey(topic.getId()))
                finalMap.put(topic.getId(), new ArrayList<>());
            finalMap.get(topic.getId()).add(users);
        }
        return finalMap;
    }

    @Override
    @RequiresAuthentication
    public Map<Long, List<UserResponse>> getCourseTeams(AuthorizationData authData, String courseID) {
        List<TeamEntity> courseTeams = teamRepository.findAllByTopicCourseID(courseID);
        Map<Long, List<UserResponse>> finalMap = new HashMap<>();

        for (TeamEntity team: courseTeams) {
            List<UserResponse> users = new ArrayList<>();
            List<String> userIDs = userInTeamRepository.findUserIDsByTeam(team);
            for (String userID: userIDs) {
                UserEntity user = userService.getStudentById(authData, userID);
                if (user != null)
                    users.add(new UserResponse(
                            user.getID(),
                            user.getFirstName(),
                            user.getMiddleNames(),
                            user.getLastName()
                    ));
            }
            finalMap.put(team.getTopic().getId(), users);
        }
        return finalMap;
    }

    @Override
    @RequiresAuthentication
    public Map<Long, List<UserResponse>> getUserTeams(AuthorizationData authData) {
        List<UserInTeamEntity> UITs = userInTeamRepository
                .findUserInTeamEntitiesByUserID(usosService.getUserData(authData).ID());

        List<TeamEntity> teams = new ArrayList<>();
        for (UserInTeamEntity uit: UITs) {
            if (uit.getTeam() != null) {
                teams.add(uit.getTeam());
            }
        }

        Map<Long, List<UserResponse>> finalMap = new HashMap<>();
        for (TeamEntity team: teams) {
            List<String> userIDs = userInTeamRepository.findUserIDsByTeam(team);
            List<UserResponse> users = new ArrayList<>();
            for (String userID: userIDs) {
                UserEntity user = userService.getStudentById(authData, userID);
                users.add(new UserResponse(
                        user.getID(),
                        user.getFirstName(),
                        user.getMiddleNames(),
                        user.getLastName()
                ));
            }
            finalMap.put(team.getTopic().getId(), users);
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

    @Override
    @RequiresAuthentication
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

    @Override
    @RequiresAuthentication
    public boolean rejectTeamRequest(AuthorizationData authData, Long teamRequestID) {
        Optional<TeamRequestEntity> teamRequest = teamRequestRepository.findById(teamRequestID);
        if (teamRequest.isEmpty())
            return false;
        List<UserInTeamEntity> teamRequestUITs = userInTeamRepository
                .findUserInTeamEntitiesByTeamRequest(teamRequest.get());
        if (teamRequest.get().getTopic().getMinTeamCap() > teamRequestUITs.size() - 1) {
            userInTeamRepository.deleteAll(teamRequestUITs);
            teamRequestRepository.deleteById(teamRequestID);
            return true;
        }

        String userID = usosService.getUserData(authData).ID();
        List<UserInTeamEntity> UITs = userInTeamRepository.findUserInTeamEntitiesByUserID(userID);
        for (UserInTeamEntity uit: UITs) {
            if (uit.getTeamRequest().equals(teamRequest.get())) {
                userInTeamRepository.delete(uit);
                return true;
            }
        }
        return false;
    }

    @Override
    @RequiresAuthentication
    public Map<Long, List<List<UserResponse>>> getUserTeamRequests(AuthorizationData authData) {
        List<UserInTeamEntity> userUITs = userInTeamRepository.
                findUserInTeamEntitiesByUserID(usosService.getUserData(authData).ID());

        Map<Long, List<List<UserResponse>>> finalMap = new HashMap<>();
        for (UserInTeamEntity uit: userUITs) {
            List<String> userIDs = userInTeamRepository.findUserIDsByTeamRequest(uit.getTeamRequest());
            List<UserResponse> users = new ArrayList<>();
            for (String userID: userIDs) {
                UserEntity user = userService.getStudentById(authData, userID);
                users.add(new UserResponse(
                        user.getID(),
                        user.getFirstName(),
                        user.getMiddleNames(),
                        user.getLastName()
                ));
            }
            if (!finalMap.containsKey(uit.getTeamRequest().getTopic().getId()))
                finalMap.put(uit.getTeamRequest().getTopic().getId(), new ArrayList<>());
            finalMap.get(uit.getTeamRequest().getTopic().getId()).add(users);
            }
        return finalMap;
        }
}
