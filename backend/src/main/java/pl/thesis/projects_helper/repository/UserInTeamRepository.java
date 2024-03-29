package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import pl.thesis.projects_helper.model.TeamEntity;
import pl.thesis.projects_helper.model.TeamRequestEntity;
import pl.thesis.projects_helper.model.UserInTeamEntity;

import java.util.List;

public interface UserInTeamRepository extends JpaRepository<UserInTeamEntity, Long> {

    @Query("SELECT u.userID FROM UserInTeamEntity u WHERE u.teamRequest = :teamRequest")
    List<String> findUserIDsByTeamRequest(@Param("teamRequest") TeamRequestEntity teamRequest);

    @Query("SELECT u.userID FROM UserInTeamEntity u WHERE u.team = :team")
    List<String> findUserIDsByTeam(@Param("team") TeamEntity team);

    @Query("SELECT u.userID FROM UserInTeamEntity u WHERE u.teamRequest.topic.id = :id")
    List<String> findUserIDsByTeamRequestTopicID(@Param("id") Long id);

    List<UserInTeamEntity> findUserInTeamEntitiesByTeamRequestTopicCourseID(String courseID);

    List<UserInTeamEntity> findUserInTeamEntitiesByUserID(String userID);

    List<UserInTeamEntity> findUserInTeamEntitiesByUserIDAndTeamIsNotNull(String userID);
    List<UserInTeamEntity> findUserInTeamEntitiesByUserIDAndTeamRequestIsNotNull(String userID);

    List<UserInTeamEntity> findUserInTeamEntitiesByTeamRequestId(Long teamRequestId);
}
