package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;

@Entity(name = "submission_team")
public class SubmissionTeam extends Submission{

    public SubmissionTeam(String titel, String comment, String participationType) {
        super(titel, comment, participationType);
    }
    @Column(name = "git_url")
    String gitUrl;
    @Column(name = "demo_url")
    String demoUrl;
    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    Team team;

}
