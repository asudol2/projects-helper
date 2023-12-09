package pl.thesis.projects_helper.model;

public class UserEntity {

    private final String ID;
    private final String firstName;
    private final String middleNames;
    private final String lastName;
    private final String sex;
    private final Integer studentStatus;
    private final Integer staffStatus;
    private final String email;

    public UserEntity(String ID,
                      String firstName,
                      String middleNames,
                      String lastName,
                      String sex,
                      Integer studentStatus,
                      Integer staffStatus,
                      String email) {
        this.ID = ID;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.sex = sex;
        this.studentStatus = studentStatus;
        this.staffStatus = staffStatus;
        this.email = email;
    }

    public UserEntity(String ID, String firstName, String lastName) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        middleNames = null;
        sex = null;
        studentStatus = null;
        staffStatus = null;
        email = null;
    }

    public String getID() {
        return ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleNames() {
        return middleNames;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSex() {
        return sex;
    }

    public Integer getStudentStatus() {
        return studentStatus;
    }

    public Integer getStaffStatus() {
        return staffStatus;
    }

    public String getEmail() {
        return email;
    }
}
