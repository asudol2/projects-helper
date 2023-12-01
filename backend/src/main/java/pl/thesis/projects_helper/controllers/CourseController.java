package pl.thesis.projects_helper.controllers;

import org.springframework.web.bind.annotation.*;
import pl.thesis.projects_helper.interfaces.ICoursesService;
import pl.thesis.projects_helper.model.CourseEntity;

import java.util.List;

@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "http://localhost:3000")
public class CourseController {
    private final ICoursesService coursesService;

    public CourseController(ICoursesService coursesService) {
        this.coursesService = coursesService;
    }

    @GetMapping("/all")
    public List<CourseEntity> getUserCourses(@RequestParam String token, @RequestParam String secret) {
        return coursesService.getAllUserCurrentRelatedCourses(token, secret);
    }

    @GetMapping("/student")
    public List<CourseEntity> getStudentCurrCourses(@RequestParam String token, @RequestParam String secret) {
        return coursesService.getCurrentStudentCourses(token, secret);
    }

    @GetMapping("/staff")
    public List<CourseEntity> getStaffCurrCourses(@RequestParam String token, @RequestParam String secret) {
        return coursesService.getCurrentStaffCourses(token, secret);
    }
}