package pl.thesis.projects_helper.interfaces;

import org.springframework.data.util.Pair;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;
import pl.thesis.projects_helper.utils.UserType;

public interface IUserService {

    UserEntity getLecturerById(AuthorizationData authData, String lecturerID);
    UserEntity getStudentById(AuthorizationData authData, String studentID);

    Pair<Integer, Integer> getUserStatusPair(AuthorizationData authData);

    boolean isCurrStudent(AuthorizationData authData);

    boolean isCurrStaff(AuthorizationData authData);
    UserType getUserType(AuthorizationData authData);
}
