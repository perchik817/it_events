package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import whz.it_events.it_eventsdbapp.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "event")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    @Column(name = "start_date")
    LocalDateTime startDate;
    @Column(name = "end_date")
    LocalDateTime endDate;
    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    Location location;
    @Enumerated (EnumType.STRING)
    Status status;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    List<Track> tracks;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    List<Session> sessions;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    List<EventSponsor> eventSponsors;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    List<Organisator> organisators;

    public Event(String name, String description, LocalDateTime startDate, LocalDateTime endDate, Location location) {
        this.name = name;
        this.description = description;
        if(startDate.isBefore(endDate) && !startDate.isBefore(LocalDateTime.now())) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
        this.location = location;
        this.status = Status.GEPLANT;
    }
}
