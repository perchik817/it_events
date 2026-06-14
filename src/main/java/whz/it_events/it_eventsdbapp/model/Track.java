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

    public Track(String name, Event event, String description, LocalDateTime deadlineDate, List<Team> teams,
                 List<Participant> participants, List<Mentor> mentors, List<Jury> juries, List<PreisSponsor> preisSponsors) {
        this.name = name;
        this.event = event;
        this.description = description;
        this.deadlineDate = deadlineDate;
        this.teams = teams;
        this.participants = participants;
        this.mentors = mentors;
        this.juries = juries;
        this.preisSponsors = preisSponsors;
    }
}
