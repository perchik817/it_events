package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "sponsor")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class Sponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String contact;
    @Column(name = "photo_url")
    String photoUrl;

    public Sponsor(String name, String contact, String photoUrl) {
        this.name = name;
        this.contact = contact;
        this.photoUrl = photoUrl;
    }
}
