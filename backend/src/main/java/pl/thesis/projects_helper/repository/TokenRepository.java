package pl.thesis.projects_helper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.thesis.projects_helper.model.LoginTokenEntity;



@Repository
public interface TokenRepository extends JpaRepository<LoginTokenEntity, Long> {
    LoginTokenEntity findByLoginToken(String loginToken);
}
