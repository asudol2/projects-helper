package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thesis.projects_helper.model.TeamEntity;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
}
