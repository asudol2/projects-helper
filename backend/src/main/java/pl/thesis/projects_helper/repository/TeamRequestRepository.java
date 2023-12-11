package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thesis.projects_helper.model.TeamRequestEntity;

public interface TeamRequestRepository extends JpaRepository<TeamRequestEntity, Long> {
}
