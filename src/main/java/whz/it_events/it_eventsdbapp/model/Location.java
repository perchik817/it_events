package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity(name = "location")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "location_name")
    String locationName;
    String stadt;
    String address;
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    List<Event> events;

    public Location(String locationName, String stadt, String address, List<Event> events) {
        this.locationName = locationName;
        this.stadt = stadt;
        this.address = address;
        this.events = events;
    }
}
