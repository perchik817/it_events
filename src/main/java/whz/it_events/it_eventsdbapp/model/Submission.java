package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import whz.it_events.it_eventsdbapp.model.enums.ParticipationType;
import whz.it_events.it_eventsdbapp.model.enums.ParticipationTypeConverter;
import whz.it_events.it_eventsdbapp.model.enums.SubmissionStatus;
import whz.it_events.it_eventsdbapp.model.enums.SubmissionStatusConverter;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "submission")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Inheritance(strategy = InheritanceType.JOINED)
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String titel;
    String comment;
    @Column(name = "submission_time")
    LocalDateTime submissionTime;
//    это я закомитила
//    @Enumerated(EnumType.STRING)
//    SubmissionStatus status;
//    @Column(name = "participation_type")
//    @Enumerated(EnumType.STRING)
//    ParticipationType participationType;
//    это я добавила
    @Convert(converter = SubmissionStatusConverter.class)
    SubmissionStatus status;

    @Column(name = "participation_type")
    @Convert(converter = ParticipationTypeConverter.class)
    ParticipationType participationType;

//    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
//    List<SubmissionTeam> submissionTeams;
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    List<Score> scores;
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    List<Participant> participants;

    public Submission(String titel, String comment, ParticipationType participationType) {
        this.titel = titel;
        this.comment = comment;
        this.status = SubmissionStatus.EINGERICHTET;
        this.participationType = participationType;
        this.submissionTime = LocalDateTime.now();
    }
}
