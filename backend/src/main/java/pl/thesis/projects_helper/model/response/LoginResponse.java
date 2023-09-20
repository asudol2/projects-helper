package pl.thesis.projects_helper.model.response;

public class LoginResponse {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String studentNumber;

    public LoginResponse(String id, String firstName, String lastName, String email, String studentNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.studentNumber = studentNumber;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getStudentNumber() {
        return studentNumber;
    }
}
