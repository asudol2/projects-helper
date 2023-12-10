package pl.thesis.projects_helper.utils;

public enum UserActivityStatus {
    ACTIVE(2),
    INACTIVE(1),
    NEVER(0);

    private final int code;

    UserActivityStatus(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
