package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity(name = "preis")
@Getter
@Setter
@NoArgsConstructor
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

    public Preis(String name, String description, String preisCategory, List<PreisSponsor> preisSponsors) {
        this.name = name;
        this.description = description;
        this.preisCategory = preisCategory;
        this.preisSponsors = preisSponsors;
    }
}
