package pl.thesis.projects_helper.interfaces;

import pl.thesis.projects_helper.model.UserEntity;

public interface IUserService {

    UserEntity getLecturerById(String lecturerID, String token, String secret);
}
