package pl.thesis.projects_helper.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users_in_teams")
public class UserInTeamEntity {
    @Embeddable
    public static class UserInTeamId implements Serializable {
        @JoinColumn(name = "team_id")
        private Long teamID;

        @JoinColumn(name = "team_request_id")
        private Long teamRequestID;

        @Column(name = "user_id", nullable = false)
        private String userID;

        public UserInTeamId() {
        }

        public Long getTeamID() {
            return teamID;
        }

        public void setTeamID(Long teamID) {
            this.teamID = teamID;
        }

        public Long getTeamRequestID() {
            return teamRequestID;
        }

        public void setTeamRequestID(Long teamRequestID) {
            this.teamRequestID = teamRequestID;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }
    }

    @EmbeddedId
    private UserInTeamId id;

    @ManyToOne(optional = true)
    @MapsId("teamID")
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private TeamEntity team;

    @ManyToOne(optional = true)
    @MapsId("teamRequestID")
    @JoinColumn(name = "team_request_id", referencedColumnName = "id")
    private TeamRequestEntity teamRequest;


    public UserInTeamEntity() {
        this.id = new UserInTeamId();
    }

    public UserInTeamEntity(Long teamID, String userID) {
        this.id = new UserInTeamId();
        this.id.teamID = teamID;
//        this.id.teamRequestID = null;
        this.id.userID = userID;

        this.team = new TeamEntity(teamID, "");
//        this.teamRequest = null;
    }

    public UserInTeamEntity(String userID, Long teamRequestID) {
        this.id = new UserInTeamId();
//        this.id.teamID = null;
        this.id.userID = userID;
        this.id.teamRequestID = teamRequestID;

//        this.team = null;
        this.teamRequest = new TeamRequestEntity(teamRequestID, "");
    }


}
