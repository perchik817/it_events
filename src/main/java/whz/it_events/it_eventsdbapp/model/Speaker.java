package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity(name = "speaker")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Speaker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String contact;
    @OneToMany(mappedBy = "speaker", cascade = CascadeType.ALL)
    List<SessionSpeaker> sessionSpeakers;

}
