package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "mentor")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    Track track;

    @Column(name = "prof_area")
    String profArea;

    public Mentor(User user, Track track, String profArea) {
        this.user = user;
        this.track = track;
        this.profArea = profArea;
    }
}
