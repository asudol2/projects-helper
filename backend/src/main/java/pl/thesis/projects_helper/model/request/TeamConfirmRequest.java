package pl.thesis.projects_helper.model.request;

public record TeamConfirmRequest (Long teamRequestId, Long topicId, Boolean confirm) {
}
