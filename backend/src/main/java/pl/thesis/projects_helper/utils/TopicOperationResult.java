package pl.thesis.projects_helper.utils;

public enum TopicOperationResult {
    SUCCESS(0, "success"),
    UNIQUE_TITLE_PER_COURSE_AND_TERM(1000, "unique_title_per_course_and_term constraint violated"),
    COURSE_ID(1001, "invalid course_id"),
    LECTURER_ID(1002, "invalid lecturer_id"),
    TERM(1003, "invalid_term"),
    TITLE(1004, "invalid_title"),
    SIZE(1005, "size overflow"),
    UNAUTHORIZED(1006, "unauthorized");

    private final int code;
    private final String message;

    TopicOperationResult(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
