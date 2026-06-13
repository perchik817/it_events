package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "track")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    String description;
    @Column(name = "deadline_time")
    LocalDateTime deadlineDate;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL)
    List<Team> teams;
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL)
    List<Participant> participants;
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL)
    List<Mentor> mentors;
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL)
    List<Jury> juries;
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL)
    List<PreisSponsor> preisSponsors;

}
