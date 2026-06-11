package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import whz.it_events.it_eventsdbapp.model.enums.SessionType;

import java.time.LocalDateTime;

@Entity(name="session")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String titel;
    String description;
    @Column(name = "start_date")
    LocalDateTime startDate;
    @Column(name = "end_date")
    LocalDateTime endDate;
    String room;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    Event event;
    @Column(name = "session_type")
    @Enumerated(EnumType.STRING)
    SessionType sessionType;
    int capacity;

    public Session(String titel, String description, LocalDateTime startDate, LocalDateTime endDate, String room,
                   Event event, SessionType sessionType, int capacity) {
        this.titel = titel;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.room = room;
        this.event = event;
        this.sessionType = sessionType;
        this.capacity = capacity;
    }
}
