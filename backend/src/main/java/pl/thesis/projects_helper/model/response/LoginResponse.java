package pl.thesis.projects_helper.model.response;

import pl.thesis.projects_helper.utils.UserType;

public record LoginResponse(String ID, String firstName, String lastName, UserType userType) {
}