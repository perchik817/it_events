package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity(name = "speaker")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Speaker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String contact;
    @OneToMany(mappedBy = "speaker", cascade = CascadeType.ALL)
    List<SessionSpeaker> sessionSpeakers;

    public Speaker(String name, String contact, List<SessionSpeaker> sessionSpeakers) {
        this.name = name;
        this.contact = contact;
        this.sessionSpeakers = sessionSpeakers;
    }
}
