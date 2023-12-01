package pl.thesis.projects_helper.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pl.thesis.projects_helper.interfaces.IUSOSService;
import pl.thesis.projects_helper.model.TopicEntity;
import pl.thesis.projects_helper.model.request.TokenRequest;
import pl.thesis.projects_helper.model.response.LoginResponse;
import pl.thesis.projects_helper.model.response.TokenResponse;
import pl.thesis.projects_helper.model.response.UsosAuthUrlResponse;
import pl.thesis.projects_helper.services.USOSService;
import org.springframework.data.util.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class USOSController {
    private final IUSOSService usosService;
    @Value("${app.frontendUrl}")
    private String frontendUrl;
    private String TEMP_loginToken;

    public USOSController(IUSOSService usosService) {
        this.usosService = usosService;
    }

    @GetMapping("/login")
    public UsosAuthUrlResponse login() {
        String loginToken = usosService.generateLoginToken();
        String authorizeUrl = usosService.getAuthorizeUrl(loginToken);
        return new UsosAuthUrlResponse(authorizeUrl, loginToken);
    }

    @GetMapping("/callback")
    public RedirectView loginSuccessCallback(@RequestParam("oauth_verifier") String oauthVerifier,
                                             @RequestParam("login_token") String loginToken) {
        TEMP_loginToken = loginToken;
        usosService.exchangeAndSaveAccessToken(oauthVerifier, loginToken);
        return new RedirectView(frontendUrl+"home");
    }
//
//    @GetMapping("/name")
//    public LoginResponse displayUserData(@RequestParam String token, @RequestParam String secret){
//        return usosService.getUserData(token, secret); // won't work after methods rework
//    }

    @PostMapping("/oauthcredentials")
    public TokenResponse getOAuthCredentials(@RequestBody TokenRequest token) {
        return usosService.getOAuthCredentials(token);
    }

    @GetMapping("/programme")
    public String getUserProgramme(){
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", new ArrayList<>());

        args.get("fields").add("id");
        args.get("fields").add("first_name");
        args.get("fields").add("last_name");
        args.get("fields").add("sex");
        args.get("fields").add("titles");
        args.get("fields").add("student_status");
        args.get("fields").add("staff_status");
        args.get("fields").add("email");
        args.get("fields").add("has_email");
        args.get("fields").add("homepage_url");
        args.get("fields").add("profile_url");
        args.get("fields").add("office_hours");
        args.get("fields").add("interests");
        args.get("fields").add("student_number");
        args.get("fields").add("student_programmes");
        args.get("fields").add("employment_functions");
        args.get("fields").add("employment_positions");
        args.get("fields").add("course_editions_conducted");
        args.get("fields").add("phd_student_status");

//        Map<String, String> output = usosService.requestUsersEndpoint(TEMP_loginToken, "user", args);
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            // Zapisz mapę do pliku JSON
//            mapper.writeValue(new File("C:\\Users\\Lenovo\\Desktop\\programme.json"), output.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "";
//        return output.toString();
    }

    @GetMapping("/courses")
    public String getUserCourses(){
        return usosService.getAllUserCurrentRelatedCourses(TEMP_loginToken).toString();
    }

    @GetMapping("/topics")
    public String getUserTopics(@RequestParam("course_id") String courseID){
        return usosService.getAllCourseCurrentRelatedTopics(courseID, TEMP_loginToken).toString();
    }

    @PostMapping("/topics")
    public boolean addOrUpdateTopic(@RequestBody TopicEntity topic){
        return usosService.addTopic(topic);
    }

    @GetMapping("/status")
    public Pair<Integer, Integer> getStatus(){
        return usosService.getUserStatusPair(TEMP_loginToken);
    }

    @GetMapping("/student/courses")
    public String getStudentCurrCourses(){
        return usosService.getCurrentStudentCourses(TEMP_loginToken).toString();
    }

    @GetMapping("/staff/courses")
    public String getStaffCurrCourses(){
        return usosService.getCurrentStaffCourses(TEMP_loginToken).toString();
    }
}