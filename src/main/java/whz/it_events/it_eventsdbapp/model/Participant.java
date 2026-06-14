package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "participant")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "track_id")
    Track track; //Olympiade, Hackathon

    @OneToOne
    @JoinColumn(name = "submission_id")
    Submission submission;

    public Participant(User user, Track track, Submission submission) {
        this.user = user;
        this.track = track;
        this.submission = submission;
    }
}
