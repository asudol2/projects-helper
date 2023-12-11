package pl.thesis.projects_helper.model;

import jakarta.persistence.*;

@Entity
@Table(name = "team_requests")
public class TeamRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private TopicEntity topic;

    public TeamRequestEntity(Long id, String field) {
        if (field.equals("topic"))
            this.topic = new TopicEntity(id);
        else this.id = id;
    }

    public TeamRequestEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TopicEntity getTopic() {
        return topic;
    }

    public void setTopic(TopicEntity topic) {
        this.topic = topic;
    }
}
