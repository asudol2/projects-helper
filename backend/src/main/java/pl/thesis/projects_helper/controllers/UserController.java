package pl.thesis.projects_helper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.thesis.projects_helper.interfaces.IUserService;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.services.UserService;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService){
        this.userService = userService;
    }

    @GetMapping("/lecturer")
    public UserEntity getLecturerById(String lecturerID){
        String token = "gjWaaFkdSdkJz5McWYcf";
        String secret = "GSUWSEWwzFEpWugjwtHgTYb6Hgnk43GqneuHQkMp";
        lecturerID = "1012113";
        return userService.getLecturerById(lecturerID, token, secret);
    }
}