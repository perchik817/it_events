package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import whz.it_events.it_eventsdbapp.model.enums.ParticipationType;

@Entity(name = "submission_team")
public class SubmissionTeam extends Submission{

    public SubmissionTeam(String titel, String comment, ParticipationType participationType) {
        super(titel, comment, participationType);
    }
    @Column(name = "git_url")
    String gitUrl;
    @Column(name = "demo_url")
    String demoUrl;
    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    Team team;

    public SubmissionTeam(String titel, String comment, ParticipationType participationType, String gitUrl,
                          String demoUrl, Team team) {
        super(titel, comment, participationType);
        this.gitUrl = gitUrl;
        this.demoUrl = demoUrl;
        this.team = team;
    }
}
