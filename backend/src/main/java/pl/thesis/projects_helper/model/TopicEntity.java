package pl.thesis.projects_helper.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "topics")
public class TopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private String courseID;

    @Column(name = "lecturer_id", columnDefinition = "default null")
    private Integer lecturerID;

    @Column(name = "term", nullable = false)
    private String term;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "default null")
    private String description;

    @Column(name = "min_team_cap", nullable = false)
    private Integer minTeamCap;

    @Column(name = "max_team_cap", nullable = false)
    private Integer maxTeamCap;

    @Column(name = "temporary", columnDefinition = "bool default False")
    private Boolean temporary;

    @Column(name = "propounder_id", columnDefinition = "default null")
    private String propounderID;

    public TopicEntity(){}

    public TopicEntity(Long id) {
        this.id = id;
    }

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
                       boolean temporary,
                       String propounderID,
                       Integer minCap,
                       Integer maxCap

    ){
        this.courseID = courseID;
        this.lecturerID = lecturerID;
        this.term = term;
        this.title = title;
        this.description = description;
        this.temporary = temporary;
        this.minTeamCap = minCap;
        this.maxTeamCap = maxCap;
        this.propounderID = propounderID;
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

    public TopicEntity(String term, String propounderID, boolean temporary) {
        this.term = term;
        this.propounderID = propounderID;
        this.temporary = temporary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicEntity that = (TopicEntity) o;
        return Objects.equals(courseID, that.courseID) &&
                Objects.equals(lecturerID, that.lecturerID) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(term, that.term) &&
                temporary == that.temporary &&
                Objects.equals(propounderID, that.propounderID) &&
                Objects.equals(minTeamCap, that.minTeamCap) &&
                Objects.equals(maxTeamCap, that.maxTeamCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseID, lecturerID, term, title, description, minTeamCap, maxTeamCap, temporary, propounderID);
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

    public Boolean isTemporary() {
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
