package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thesis.projects_helper.model.TeamEntity;

import java.util.List;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    List<TeamEntity> findAllByTopicCourseID(String courseID);


}
