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
    private Integer lecturerID;

    @Column(name = "term")
    private String term;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "min_team_cap")
    private Integer minTeamCap;

    @Column(name = "max_team_cap")
    private Integer maxTeamCap;

    @Column(name = "temporary")
    private Boolean temporary;

    @Column(name = "propounder_id")
    private String propounderID;

    public TopicEntity(){}

    public TopicEntity(String courseID, Integer lecturerID, String term, String title, String description,
                       Integer minTeamCap, Integer maxTeamCap, Boolean temporary, String propounderID) {
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

    public TopicEntity(String courseID,
                       Integer lecturerID,
                       String title,
                       String description,
                       String term,
                       Boolean temporary
    ){
        this.courseID = courseID;
        this.lecturerID = lecturerID;
        this.term = term;
        this.title = title;
        this.description = description;
        this.temporary = temporary;
    }

    public TopicEntity(String courseID, Integer lecturerID, String term, String title) {
        this.courseID = courseID;
        this.lecturerID = lecturerID;
        this.term = term;
        this.title = title;
    }

    public TopicEntity(Long id, String courseID, Integer lecturerID, String term, String title,
                       String description, Integer minTeamCap, Integer maxTeamCap, Boolean temporary, String propounderID) {
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

    public Integer getLecturerID() {
        return lecturerID;
    }

    public void setLecturerID(Integer lecturerID) {
        this.lecturerID = lecturerID;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
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

    public Integer getMinTeamCap() {
        return minTeamCap;
    }

    public void setMinTeamCap(Integer minTeamCap) {
        this.minTeamCap = minTeamCap;
    }

    public Integer getMaxTeamCap() {
        return maxTeamCap;
    }

    public void setMaxTeamCap(Integer maxTeamCap) {
        this.maxTeamCap = maxTeamCap;
    }

    public Boolean getTemporary() {
        return temporary;
    }

    public void setTemporary(Boolean temporary) {
        this.temporary = temporary;
    }

    public String getPropounderID() {
        return propounderID;
    }

    public void setPropounderID(String propounderID) {
        this.propounderID = propounderID;
    }
}
