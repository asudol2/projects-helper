package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import pl.thesis.projects_helper.model.TeamRequestEntity;

import java.util.List;
import java.util.Optional;

public interface TeamRequestRepository extends JpaRepository<TeamRequestEntity, Long> {

    @Query("SELECT tr FROM TeamRequestEntity tr WHERE tr.topic.courseID = :courseID")
    List<TeamRequestEntity> findAllByCourseID(@Param("courseID") String courseID);

    Optional<TeamRequestEntity> findById(Long id);
    void deleteById(Long id);
}
