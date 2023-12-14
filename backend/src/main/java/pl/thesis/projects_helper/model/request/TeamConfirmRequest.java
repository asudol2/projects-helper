package pl.thesis.projects_helper.model.request;

import java.util.List;

public record TeamConfirmRequest (String courseID, String title, List<String> userIDs, Boolean confirm) {
}
