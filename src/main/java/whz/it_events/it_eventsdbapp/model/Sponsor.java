package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity(name = "sponsor")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String contact;
    @Column(name = "photo_url")
    String photoUrl;
}
