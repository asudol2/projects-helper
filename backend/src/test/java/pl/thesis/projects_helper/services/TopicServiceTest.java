package pl.thesis.projects_helper.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import pl.thesis.projects_helper.model.CourseEntity;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.request.TopicConfirmRequest;
import pl.thesis.projects_helper.model.request.TopicRequest;
import pl.thesis.projects_helper.repository.TopicRepository;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.thesis.projects_helper.utils.TopicOperationResult;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {

    private TopicService spyTopicService;

    private CoursesService spyCoursesService;

    private UserService spyUserService;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        this.spyCoursesService = spy(new CoursesService(new RestTemplate()));
        this.spyUserService = spy(new UserService(new RestTemplate()));
        this.spyTopicService = spy(new TopicService(spyCoursesService));
        ReflectionTestUtils.setField(spyTopicService, "topicRepository", topicRepository);
        ReflectionTestUtils.setField(spyTopicService, "userService", spyUserService);
    }

    public JsonNode createEmptyJsonNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.createObjectNode();
    }

    @Test
    public void getAllUserCurrentRelatedTopicsTest() {
        List<Map<String, Object>> mockGroup = Arrays.asList(
                Map.of("course_id", "GKOM"),
                Map.of("course_id", "IUM"),
                Map.of("course_id", "FO"),
                Map.of("course_id", "AIS")
        );
        List<TopicEntity> topicsGKOM = Arrays.asList(
                new TopicEntity(0L),
                new TopicEntity(1L),
                new TopicEntity(2L)
        );
        List<TopicEntity> topicsIUM = Arrays.asList(
                new TopicEntity(10L),
                new TopicEntity(11L)
        );
        List<TopicEntity> topicsFO = Arrays.asList(
                new TopicEntity(20L)
        );
        List<TopicEntity> topicsAIS = Arrays.asList(
                new TopicEntity(30L),
                new TopicEntity(31L),
                new TopicEntity(32L),
                new TopicEntity(33L),
                new TopicEntity(34L)
        );

        doReturn(createEmptyJsonNode()).when(spyCoursesService).requestGroupsEndpoint(
                any(AuthorizationData.class),
                any(String.class),
                any(HashMap.class));

        doReturn(mockGroup).when(spyCoursesService).retrieveCurrentCoursesGroup(any());
        doReturn(topicsGKOM).when(topicRepository).findAllByCourseID("GKOM");
        doReturn(topicsIUM).when(topicRepository).findAllByCourseID("IUM");
        doReturn(topicsFO).when(topicRepository).findAllByCourseID("FO");
        doReturn(topicsAIS).when(topicRepository).findAllByCourseID("AIS");

        List<TopicEntity> expectedTopics = new ArrayList<>();
        expectedTopics.addAll(topicsFO);
        expectedTopics.addAll(topicsAIS);
        expectedTopics.addAll(topicsGKOM);
        expectedTopics.addAll(topicsIUM);

        List<TopicEntity> actualTopics = spyTopicService.getAllUserCurrentRelatedTopics(
                new AuthorizationData("token", "secret"));

        assertThat(actualTopics).containsExactlyInAnyOrderElementsOf(expectedTopics);
    }

    @Test
    public void getAllCourseCurrentRelatedTopicsTest() {
        TopicEntity topic1 = new TopicEntity("FO", 1234, "2023L", "title1");
        TopicEntity topic2 = new TopicEntity("GKOM", 2345, "2023L", "title2");
        TopicEntity topic3 = new TopicEntity("FO", 1234, "2023L", "title3");
        TopicEntity topic4 = new TopicEntity("AIS", 3456, "2023L", "title4");

        AuthorizationData authData = new AuthorizationData("token", "secret");
        List<TopicEntity> mockedTopics = Arrays.asList(topic1, topic2, topic3, topic4);
        List<TopicEntity> expectedTopics = Arrays.asList(topic1, topic3);

        doReturn(mockedTopics).when(spyTopicService).getAllUserCurrentRelatedTopics(any(AuthorizationData.class));
        List<TopicEntity> actualTopics = spyTopicService.getAllCourseCurrentRelatedTopics("FO", authData);

        assertThat(actualTopics).containsExactlyInAnyOrderElementsOf(expectedTopics);
    }

    @Test
    public void isAuthorizedToManipulateTopicSuccessTest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicEntity topic = new TopicEntity("FO", 1234, "2023Z", "title1");
        List<CourseEntity> courses = Arrays.asList(
                new CourseEntity("GKOM"),
                new CourseEntity("IUM"),
                new CourseEntity("AIS"),
                new CourseEntity("FPPI"),
                new CourseEntity("WUS"),
                new CourseEntity("FO"),
                new CourseEntity("PROB")
        );

        doReturn(courses).when(spyCoursesService).getAllUserCurrentRelatedCourses(any(AuthorizationData.class));
        assertThat(spyTopicService.isAuthorizedToManipulateTopic(topic, authData)).isTrue();
    }

    @Test
    public void isAuthorizedToManipulateTopicFailTest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicEntity topic = new TopicEntity("FO", 1234, "2023Z", "title1");
        List<CourseEntity> courses = Arrays.asList(
                new CourseEntity("GKOM"),
                new CourseEntity("IUM"),
                new CourseEntity("AIS"),
                new CourseEntity("FPPI"),
                new CourseEntity("WUS"),
                new CourseEntity("PROB")
        );

        doReturn(courses).when(spyCoursesService).getAllUserCurrentRelatedCourses(any(AuthorizationData.class));
        assertThat(spyTopicService.isAuthorizedToManipulateTopic(topic, authData)).isFalse();
    }

    @Test
    public void createTopicEntityFromTopicRequestTest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicRequest topicRequest = new TopicRequest(
                "FO",
                1234,
                "title1",
                "description1",
                1,
                4
        );

        doReturn(true).when(spyUserService).isCurrStaff(any(AuthorizationData.class));
        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn("1158935").when(spyTopicService).getUserID(any(AuthorizationData.class));

        TopicEntity expectedTopic = new TopicEntity(
                "FO",
                1234,
                "title1",
                "description1",
                "2023Z",
                false,
                "1158935",
                1,
                4
        );
        TopicEntity actualTopic = spyTopicService.createTopicEntityFromTopicRequest(authData, topicRequest);
        assertThat(actualTopic).isEqualTo(expectedTopic);
    }

    @Test
    public void addTopicInvalidatedRequestTest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicRequest topicRequest = new TopicRequest(
                "FO",
                1234,
                "title1",
                "description1",
                4,
                1
        );
        TopicOperationResult expectedResult = TopicOperationResult.MIN_TEAM_CAP;
        assertThat(spyTopicService.addTopic(authData, topicRequest)).isEqualTo(expectedResult);
    }

    @Test
    public void addTopicUnauthorizedTest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicRequest topicRequest = new TopicRequest(
                "FO",
                1234,
                "title1",
                "description1",
                1,
                4
        );

        doReturn(true).when(spyUserService).isCurrStaff(any(AuthorizationData.class));
        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn("1158935").when(spyTopicService).getUserID(any(AuthorizationData.class));
        doReturn(false).when(spyTopicService).isAuthorizedToManipulateTopic(
                any(TopicEntity.class),
                any(AuthorizationData.class)
        );

        TopicOperationResult expectedResult = TopicOperationResult.UNAUTHORIZED;
        assertThat(spyTopicService.addTopic(authData, topicRequest)).isEqualTo(expectedResult);
    }

    @Test
    public void addTopicDBErrorTest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicRequest topicRequest = new TopicRequest(
                "FO",
                1234,
                "title1",
                "description1",
                1,
                4
        );

        doReturn(true).when(spyUserService).isCurrStaff(any(AuthorizationData.class));
        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn("1158935").when(spyTopicService).getUserID(any(AuthorizationData.class));
        doReturn(true).when(spyTopicService).isAuthorizedToManipulateTopic(
                any(TopicEntity.class),
                any(AuthorizationData.class)
        );
        doAnswer(invocation -> {
            throw new Exception("ERROR MESSAGE unique_title_per_course_and_term; ENDSNOW");
        }).when(topicRepository).save(any(TopicEntity.class));

        TopicOperationResult expectedResult = TopicOperationResult.UNIQUE_TITLE_PER_COURSE_AND_TERM;
        assertThat(spyTopicService.addTopic(authData, topicRequest)).isEqualTo(expectedResult);
    }

    @Test
    public void addTopicSuccessTest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicRequest topicRequest = new TopicRequest(
                "FO",
                1234,
                "title1",
                "description1",
                1,
                4
        );

        doReturn(true).when(spyUserService).isCurrStaff(any(AuthorizationData.class));
        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn("1158935").when(spyTopicService).getUserID(any(AuthorizationData.class));
        doReturn(true).when(spyTopicService).isAuthorizedToManipulateTopic(
                any(TopicEntity.class),
                any(AuthorizationData.class)
        );

        ArgumentCaptor<TopicEntity> topicEntityCaptor = ArgumentCaptor.forClass(TopicEntity.class);
        when(topicRepository.save(topicEntityCaptor.capture())).thenReturn(mock(TopicEntity.class));

        TopicOperationResult expectedResult = TopicOperationResult.SUCCESS;
        assertThat(spyTopicService.addTopic(authData, topicRequest)).isEqualTo(expectedResult);

        TopicEntity expectedSavedTopic = new TopicEntity(
                "FO",
                1234,
                "title1",
                "description1",
                "2023Z",
                false,
                "1158935",
                1,
                4
        );
        assertThat(topicEntityCaptor.getValue()).isEqualTo(expectedSavedTopic);
    }

    @Test
    public void getSelectiveStudentTopicsByCourseTest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicEntity topic1 = new TopicEntity("2023Z", "1158935", true);
        TopicEntity topic2 = new TopicEntity("2023Z", "1158935", false);
        TopicEntity topic3 = new TopicEntity("2023Z", "1234567", true);
        TopicEntity topic4 = new TopicEntity("2023Z", "1234567", false);
        TopicEntity topic5 = new TopicEntity("2022L", "1158935", true);
        TopicEntity topic6 = new TopicEntity("2022L", "1158935", false);
        TopicEntity topic7 = new TopicEntity("2022L", "1234567", true);
        TopicEntity topic8 = new TopicEntity("2022L", "1234567", false);
        List<TopicEntity> topics = new ArrayList<>(
                List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8)
        );

        List<TopicEntity> expectedTopics = Arrays.asList(topic1, topic2, topic4);

        doReturn(topics).when(spyTopicService).getAllCourseCurrentRelatedTopics(
                any(String.class), any(AuthorizationData.class)
        );
        doReturn("1158935").when(spyTopicService).getUserID(any(AuthorizationData.class));
        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));

        assertThat(spyTopicService.getSelectiveStudentTopicsByCourse(authData, "FO"))
                .containsExactlyInAnyOrderElementsOf(expectedTopics);
    }

    @Test
    public void getSelectiveLecturerTopicsByCourseTest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicEntity topic1 = new TopicEntity("2023Z", "1158935", true);
        TopicEntity topic2 = new TopicEntity("2023Z", "1158935", false);
        TopicEntity topic3 = new TopicEntity("2023Z", "1234567", true);
        TopicEntity topic4 = new TopicEntity("2023Z", "1234567", false);
        TopicEntity topic5 = new TopicEntity("2022L", "1158935", true);
        TopicEntity topic6 = new TopicEntity("2022L", "1158935", false);
        TopicEntity topic7 = new TopicEntity("2022L", "1234567", true);
        TopicEntity topic8 = new TopicEntity("2022L", "1234567", false);
        List<TopicEntity> topics = new ArrayList<>(
                List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8)
        );

        List<TopicEntity> expectedTopics = Arrays.asList(topic1, topic2, topic3, topic4);

        doReturn(topics).when(spyTopicService).getAllCourseCurrentRelatedTopics(
                any(String.class), any(AuthorizationData.class)
        );
        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));

        assertThat(spyTopicService.getSelectiveLecturerTopicsByCourse(authData, "FO"))
                .containsExactlyInAnyOrderElementsOf(expectedTopics);
    }

    @Test
    public void getSelectiveUserTopicsByCourseStaff() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicEntity topic1 = new TopicEntity("2023Z", "1158935", true);
        TopicEntity topic2 = new TopicEntity("2023Z", "1158935", false);
        TopicEntity topic3 = new TopicEntity("2023Z", "1234567", true);
        TopicEntity topic4 = new TopicEntity("2023Z", "1234567", false);
        TopicEntity topic5 = new TopicEntity("2022L", "1158935", true);
        TopicEntity topic6 = new TopicEntity("2022L", "1158935", false);
        TopicEntity topic7 = new TopicEntity("2022L", "1234567", true);
        TopicEntity topic8 = new TopicEntity("2022L", "1234567", false);
        List<TopicEntity> topics = new ArrayList<>(
                List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8)
        );

        List<TopicEntity> expectedTopics = Arrays.asList(topic1, topic2, topic3, topic4);

        doReturn(topics).when(spyTopicService).getAllCourseCurrentRelatedTopics(
                any(String.class), any(AuthorizationData.class)
        );
        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn(true).when(spyUserService).isCurrStaff(any(AuthorizationData.class));

        assertThat(spyTopicService.getSelectiveUserTopicsByCourse(authData, "FO"))
                .containsExactlyInAnyOrderElementsOf(expectedTopics);
    }

    @Test
    public void getSelectiveUserTopicsByCourseStudent() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicEntity topic1 = new TopicEntity("2023Z", "1158935", true);
        TopicEntity topic2 = new TopicEntity("2023Z", "1158935", false);
        TopicEntity topic3 = new TopicEntity("2023Z", "1234567", true);
        TopicEntity topic4 = new TopicEntity("2023Z", "1234567", false);
        TopicEntity topic5 = new TopicEntity("2022L", "1158935", true);
        TopicEntity topic6 = new TopicEntity("2022L", "1158935", false);
        TopicEntity topic7 = new TopicEntity("2022L", "1234567", true);
        TopicEntity topic8 = new TopicEntity("2022L", "1234567", false);
        List<TopicEntity> topics = new ArrayList<>(
                List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8)
        );

        List<TopicEntity> expectedTopics = Arrays.asList(topic1, topic2, topic4);

        doReturn(topics).when(spyTopicService).getAllCourseCurrentRelatedTopics(
                any(String.class), any(AuthorizationData.class)
        );
        doReturn("1158935").when(spyTopicService).getUserID(any(AuthorizationData.class));
        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn(false).when(spyUserService).isCurrStaff(any(AuthorizationData.class));
        doReturn(true).when(spyUserService).isCurrStudent(any(AuthorizationData.class));

        assertThat(spyTopicService.getSelectiveUserTopicsByCourse(authData, "FO"))
                .containsExactlyInAnyOrderElementsOf(expectedTopics);
    }

    @Test
    public void getSelectiveUserTopicsByCourseWrongUser() {
        AuthorizationData authData = new AuthorizationData("", "");
        doReturn(false).when(spyUserService).isCurrStaff(any(AuthorizationData.class));
        doReturn(false).when(spyUserService).isCurrStudent(any(AuthorizationData.class));

        assertThat(spyTopicService.getSelectiveUserTopicsByCourse(authData, "FO"))
                .isNull();
    }

    @Test
    public void confirmTemporaryTopicWrongRequest() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicConfirmRequest topicRequest = new TopicConfirmRequest("FO", "title1", true);
        Optional<TopicEntity> optTopic = Optional.empty();

        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn(optTopic).when(topicRepository)
                .findByCourseIDAndTermAndTitle(any(String.class), any(String.class), any(String.class));

        assertThat(spyTopicService.confirmTemporaryTopic(authData, topicRequest)).isFalse();
    }

    @Test
    public void confirmTemporaryTopicDeleteNotTemporary() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicConfirmRequest topicRequest = new TopicConfirmRequest("FO", "title1", false);
        Optional<TopicEntity> optTopic = Optional
                .of(new TopicEntity("2023Z", "1158935", false));

        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn(optTopic).when(topicRepository)
                .findByCourseIDAndTermAndTitle(any(String.class), any(String.class), any(String.class));

        assertThat(spyTopicService.confirmTemporaryTopic(authData, topicRequest)).isFalse();
    }

    @Test
    public void confirmTemporaryTopicDelete() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicConfirmRequest topicRequest = new TopicConfirmRequest("FO", "title1", false);
        Optional<TopicEntity> optTopic = Optional
                .of(new TopicEntity("2023Z", "1158935", true));

        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn(optTopic).when(topicRepository)
                .findByCourseIDAndTermAndTitle(any(String.class), any(String.class), any(String.class));

        assertThat(spyTopicService.confirmTemporaryTopic(authData, topicRequest)).isTrue();
    }

    @Test
    public void confirmTemporaryTopicSuccess() {
        AuthorizationData authData = new AuthorizationData("", "");
        TopicConfirmRequest topicRequest = new TopicConfirmRequest("FO", "title1", true);
        TopicEntity retrievedTopic = new TopicEntity(
                "FO",
                1234,
                "title1",
                "description1",
                "2023Z",
                true,
                "1158935",
                2,
                3
        );
        Optional<TopicEntity> optTopic = Optional.of(retrievedTopic);
        TopicEntity expectedSavedTopic = new TopicEntity(
                "FO",
                1234,
                "title1",
                "description1",
                "2023Z",
                false,
                "1158935",
                2,
                3
        );

        doReturn("2023Z").when(spyCoursesService).retrieveCurrentTerm(any(AuthorizationData.class));
        doReturn(optTopic).when(topicRepository)
                .findByCourseIDAndTermAndTitle(any(String.class), any(String.class), any(String.class));

        ArgumentCaptor<TopicEntity> topicEntityCaptor = ArgumentCaptor.forClass(TopicEntity.class);
        when(topicRepository.save(topicEntityCaptor.capture())).thenReturn(mock(TopicEntity.class));

        assertThat(spyTopicService.confirmTemporaryTopic(authData, topicRequest)).isTrue();
        assertThat(topicEntityCaptor.getValue()).isEqualTo(expectedSavedTopic);
    }
}
