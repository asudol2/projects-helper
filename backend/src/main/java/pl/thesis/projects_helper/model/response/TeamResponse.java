package pl.thesis.projects_helper.model.response;

import java.util.List;

public record TeamResponse (Long teamRequestId, Long topicId, String topicTitle, String courseName, List<UserResponse> participants
) {}