package pl.thesis.projects_helper.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.model.CourseEntity;
import pl.thesis.projects_helper.model.response.ParticipantResponse;
import pl.thesis.projects_helper.services.AuthorizationService;

import java.util.List;

@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "http://localhost:3000")
public class CourseController {
    private final ICoursesService coursesService;
    private final AuthorizationService authorizationService;


    public CourseController(ICoursesService coursesService, AuthorizationService authorizationService) {
        this.coursesService = coursesService;
        this.authorizationService = authorizationService;
    }

    @GetMapping("")
    public List<CourseEntity> getUserCourses(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return coursesService.getAllUserCurrentRelatedCourses(authData);
    }

    @GetMapping("/student")
    public List<CourseEntity> getStudentCurrCourses(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return coursesService.getCurrentStudentCourses(authData);
    }

    @GetMapping("/staff")
    public List<CourseEntity> getStaffCurrCourses(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return coursesService.getCurrentStaffCourses(authData);
    }

    @GetMapping("/term")
    public String getCurrentTerm(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return coursesService.retrieveCurrentTerm(authData);
    }

    @GetMapping("/lecturers")
    public List<ParticipantResponse> getCourseLecturers(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("course_id") String courseID){
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return coursesService.retrieveCurrentCourseLecturers(authData, courseID);
    }

    @GetMapping("/participants")
    public List<ParticipantResponse> getCourseParticipants(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("course_id") String courseID){
        AuthorizationService.AuthorizationData authData =
                authorizationService.processAuthorizationHeader(authorizationHeader);
        return coursesService.retrieveCurrentCourseParticipants(authData, courseID);
    }
}
