package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thesis.projects_helper.model.TopicEntity;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {

    TopicEntity findTopicById(int id);

    List<TopicEntity> findAllByCourseID(String courseID);

    Optional<TopicEntity> findByCourseIDAndTermAndTitle(String courseID, String term, String title);

}
