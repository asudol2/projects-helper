package pl.thesis.projects_helper.utils;

public enum TopicOperationResult {
    SUCCESS(0, "success"),
    UNIQUE_TITLE_PER_COURSE_AND_TERM(1000, "unique_title_per_course_and_term constraint violated"),
    SIZE(1001, "size overflow"),
    ID(2000, "invalid id"),
    COURSE_ID(3000, "invalid course_id"),
    COURSE_ID_SIZE(3001, "course_id too long"),
    TERM(4000, "invalid term"),
    TERM_SIZE(4001, "term too long"),
    TITLE(5000, "invalid title"),
    TITLE_SIZE(5001, "title too long"),
    LECTURER_ID(6000, "invalid lecturer_id"),
    DESCRIPTION_SIZE(7000, "description too long"),
    MIN_TEAM_CAP(7000, "invalid min_team_cap"),
    MAX_TEAM_CAP(8000, "invalid max_team_cap"),
    UNAUTHORIZED(401, "unauthorized");

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
