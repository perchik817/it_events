package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity(name = "preis")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    @Column(name = "preis_category")
    String preisCategory;
    @OneToMany(mappedBy = "preis", cascade = CascadeType.ALL)
    List<PreisSponsor> preisSponsors;

}
