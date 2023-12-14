package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.UserEntity;

public interface IUserService {

    UserEntity getLecturerById(String token, String secret, String lecturerID);
    UserEntity getStudentById(String token, String secret, String studentID);
}
