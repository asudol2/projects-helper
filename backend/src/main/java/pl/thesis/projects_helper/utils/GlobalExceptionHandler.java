package pl.thesis.projects_helper.utils;


public class GlobalExceptionHandler {

    public static void handleRuntimeException(String errorMessage) {
        throw new RuntimeException(errorMessage);
    }

}