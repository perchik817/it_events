package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "session_speaker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionSpeaker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String topic;
    @ManyToOne
    @JoinColumn(name = "speaker_id", referencedColumnName = "id")
    Speaker speaker;
    @ManyToOne
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    Session session;

}
