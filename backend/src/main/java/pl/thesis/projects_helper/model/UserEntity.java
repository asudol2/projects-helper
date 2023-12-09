package pl.thesis.projects_helper.model;

public record UserEntity(String ID, String firstName, String middleNames, String lastName, String sex,
                         Integer studentStatus, Integer staffStatus, String email) {

}
