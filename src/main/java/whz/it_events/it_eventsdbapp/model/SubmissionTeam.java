package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import whz.it_events.it_eventsdbapp.model.enums.ParticipationType;

@Entity(name = "submission_team")
@Getter
@Setter
@NoArgsConstructor
public class SubmissionTeam extends Submission {

    @Column(name = "git_url")
    String gitUrl;
    @Column(name = "demo_url")
    String demoUrl;
    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    Team team;

    public SubmissionTeam(String titel, String comment, ParticipationType participationType) {
        super(titel, comment, participationType);
    }

    public SubmissionTeam(String titel, String comment, ParticipationType participationType,
                          String gitUrl, String demoUrl, Team team) {
        super(titel, comment, participationType);
        this.gitUrl = gitUrl;
        this.demoUrl = demoUrl;
        this.team = team;
    }
}
