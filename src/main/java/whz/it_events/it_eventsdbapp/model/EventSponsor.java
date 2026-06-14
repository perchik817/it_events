package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "event_sponsor")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String fee;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "sponsor_id", referencedColumnName = "id")
    Sponsor sponsor;

    public EventSponsor(String fee, Event event, Sponsor sponsor) {
        this.fee = fee;
        this.event = event;
        this.sponsor = sponsor;
    }
}

