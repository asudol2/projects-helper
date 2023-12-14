package pl.thesis.projects_helper.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users_in_teams")
public class UserInTeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    @ManyToOne
    @JoinColumn(name = "team_request_id")
    private TeamRequestEntity teamRequest;

    @Column(name = "user_id", nullable = false)
    private String userID;

    public UserInTeamEntity(TeamEntity team, String userID) {
        this.team = team;
        this.userID = userID;
    }

    public UserInTeamEntity(TeamRequestEntity teamRequest, String userID) {
        this.teamRequest = teamRequest;
        this.userID = userID;
    }

    public UserInTeamEntity(Long id) {
        this.id = id;
    }

    public UserInTeamEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public TeamRequestEntity getTeamRequest() {
        return teamRequest;
    }

    public void setTeamRequest(TeamRequestEntity teamRequest) {
        this.teamRequest = teamRequest;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
