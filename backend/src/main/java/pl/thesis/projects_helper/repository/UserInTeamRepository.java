package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thesis.projects_helper.model.UserInTeamEntity;

public interface UserInTeamRepository extends JpaRepository<UserInTeamEntity, Long> {
}
