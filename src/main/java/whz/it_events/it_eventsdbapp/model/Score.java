package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity(name = "score")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String criteria;
    @Column(name = "score_value")
    Integer scoreValue;
    String comment;
    @Column(name = "review_date")
    LocalDateTime reviewDate;
    @ManyToOne
    @JoinColumn(name = "submission_id", referencedColumnName = "id")
    Submission submission;
    @ManyToOne
    @JoinColumn(name = "jury_id", referencedColumnName = "id")
    Jury jury;

    public Score(String criteria, Integer scoreValue, String comment, LocalDateTime reviewDate, Submission submission, Jury jury) {
        this.criteria = criteria;
        this.scoreValue = scoreValue;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.submission = submission;
        this.jury = jury;
    }
}
