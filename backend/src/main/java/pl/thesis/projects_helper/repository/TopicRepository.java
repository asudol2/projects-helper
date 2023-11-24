package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thesis.projects_helper.model.TopicEntity;

import java.util.List;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {

    TopicEntity findByCourseID(String courseID);

    List<TopicEntity> findAllByCourseID(String courseID);

    TopicEntity findByLecturerID(int lecturerID);
}
