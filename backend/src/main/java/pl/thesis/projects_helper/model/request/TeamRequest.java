package pl.thesis.projects_helper.model.request;

import java.util.List;

public record TeamRequest(String courseID, String title, List<String> userIDs) {
}
