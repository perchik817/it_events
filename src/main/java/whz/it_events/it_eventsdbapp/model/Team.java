package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "team")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(name = "score_value")
    int scoreValue;
    @Column(name = "registration_date")
    LocalDateTime registrationDate;
    @ManyToOne
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    Track track;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    List<Member> members;
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    List<SubmissionTeam> submissionTeams;

    public Team(String name, Track track) {
        this.name = name;
        this.scoreValue = 0;
        this.registrationDate = LocalDateTime.now();
        this.track = track;
    }
}
