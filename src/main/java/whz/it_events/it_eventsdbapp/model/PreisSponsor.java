package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity(name = "preis_sponsor")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PreisSponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    Track track;

    @ManyToOne
    @JoinColumn(name = "preis_id", referencedColumnName = "id")
    Preis preis;

    @ManyToOne
    @JoinColumn(name = "sponsor_id", referencedColumnName = "id")
    Sponsor sponsor;

}
