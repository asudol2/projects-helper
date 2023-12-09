package pl.thesis.projects_helper.model;

import jakarta.persistence.*;

@Entity
@Table(name = "topics")
public class TopicEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topic_sequence")
    @SequenceGenerator(name = "topic_sequence", sequenceName = "public.topics_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "course_id")
    private String courseID;

    @Column(name = "lecturer_id")
    private int lecturerID;

    @Column(name = "term")
    private String term;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "min_team_cap")
    private int minTeamCap;

    @Column(name = "max_team_cap")
    private int maxTeamCap;

    @Column(name = "temporary")
    private boolean temporary;

    @Column(name = "propounder_id")
    private String propounderID;

    public TopicEntity(String courseID,
                       int lecturerID,
                       String title,
                       String description,
                       int minTeamCap,
                       int maxTeamCap,
                       boolean temporary
                       ){
        this.courseID = courseID;
        this.lecturerID = lecturerID;
        this.title = title;
        this.description = description;
        this.minTeamCap = minTeamCap;
        this.maxTeamCap = maxTeamCap;
        this.temporary = temporary;
    }

    public TopicEntity(String courseID,
                       int lecturerID,
                       String title,
                       String description
    ){
        this.courseID = courseID;
        this.lecturerID = lecturerID;
        this.title = title;
        this.description = description;
    }

    public TopicEntity(Long id,
                       String courseID,
                       int lecturerID,
                       String title,
                       String description,
                       int minTeamCap,
                       int maxTeamCap,
                       boolean temporary
    ){
        this.id = id;
        this.courseID = courseID;
        this.lecturerID = lecturerID;
        this.title = title;
        this.description = description;
        this.minTeamCap = minTeamCap;
        this.maxTeamCap = maxTeamCap;
        this.temporary = temporary;
    }

    public TopicEntity(){}

    public TopicEntity(Long id, String courseID, int lecturerID, String term, String title, String description,
                       int minTeamCap, int maxTeamCap, boolean temporary, String propounderID) {
        this.id = id;
        this.courseID = courseID;
        this.lecturerID = lecturerID;
        this.term = term;
        this.title = title;
        this.description = description;
        this.minTeamCap = minTeamCap;
        this.maxTeamCap = maxTeamCap;
        this.temporary = temporary;
        this.propounderID = propounderID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public int getLecturerID() {
        return lecturerID;
    }

    public void setLecturerID(int lecturerID) {
        this.lecturerID = lecturerID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinTeamCap() {
        return minTeamCap;
    }

    public void setMinTeamCap(int minTeamCap) {
        this.minTeamCap = minTeamCap;
    }

    public int getMaxTeamCap() {
        return maxTeamCap;
    }

    public void setMaxTeamCap(int maxTeamCap) {
        this.maxTeamCap = maxTeamCap;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public String getTerm() {
        return term;
    }

    public String getPropounderID() {
        return propounderID;
    }
}
