package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import pl.thesis.projects_helper.model.TeamRequestEntity;
import pl.thesis.projects_helper.model.UserInTeamEntity;
import pl.thesis.projects_helper.model.TeamEntity;

import java.util.List;

public interface UserInTeamRepository extends JpaRepository<UserInTeamEntity, Long> {

    @Query("SELECT u.userID FROM UserInTeamEntity u WHERE u.teamRequest = :teamRequest")
    List<String> findUserIDsByTeamRequest(@Param("teamRequest") TeamRequestEntity teamRequest);

    List<UserInTeamEntity> findUserInTeamEntitiesByUserIDIsIn(List<String> userIDs);
}
