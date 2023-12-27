package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thesis.projects_helper.model.TeamRequestEntity;

import java.util.List;
import java.util.Optional;

public interface TeamRequestRepository extends JpaRepository<TeamRequestEntity, Long> {

    List<TeamRequestEntity> findByTopicCourseID(String courseID);

    List<TeamRequestEntity> findByTopicId(Long topicID);

    Optional<TeamRequestEntity> findById(Long id);
    void deleteById(Long id);
}
