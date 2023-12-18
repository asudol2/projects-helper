package pl.thesis.projects_helper.utils;

public enum TeamRequestValidationResult {

    NONUNIQUE(1000, "nonunique users' ids"),
    NO_TOPIC(1001, "no referential topic"),
    TEMP_TOPIC(1002, "referential topic is temporary"),
    SIZE_ERR(1003, "users' ids number not in range"),
    SAME_TEAM_REQ(1004, "the same team request already exists"),
    SUCCESS(0, "success");

    private final int code;
    private final String message;

    TeamRequestValidationResult(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() { return message; }
}
