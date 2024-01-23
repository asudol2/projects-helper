package pl.thesis.projects_helper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.IUserService;
import pl.thesis.projects_helper.model.UserEntity;
import pl.thesis.projects_helper.services.AuthorizationService;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000", "http://13.93.65.60:3000", "http://13.93.65.60", "http://10.1.0.4:3000", "http://10.1.0.4"})
public class UserController {

    private final IUserService userService;

    private final AuthorizationService authServ;

    @Autowired
    public UserController(IUserService userService, AuthorizationService authorizationService){
        this.userService = userService;
        this.authServ = authorizationService;
    }

    @GetMapping("/lecturer")
    public UserEntity getLecturerById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("lecturer_id") String lecturerID){
        AuthorizationService.AuthorizationData authData = authServ.processAuthorizationHeader(authorizationHeader);
        return userService.getLecturerById(authData, lecturerID);
    }

    @GetMapping("/student")
    public UserEntity getStudentById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                      @RequestParam("student_id") String studentID){
        AuthorizationService.AuthorizationData authData = authServ.processAuthorizationHeader(authorizationHeader);
        return userService.getStudentById(authData, studentID);
    }
}