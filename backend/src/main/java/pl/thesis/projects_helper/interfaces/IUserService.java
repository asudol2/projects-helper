package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.services.AuthorizationService.AuthorizationData;

public interface IUserService {

    UserEntity getLecturerById(AuthorizationData authData, String lecturerID);
    UserEntity getStudentById(AuthorizationData authData, String studentID);
}
