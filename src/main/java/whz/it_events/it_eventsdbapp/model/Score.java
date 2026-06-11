package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity(name = "score")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
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

    public Score(Submission submission, String criteria, Integer scoreValue) {
        this.submission = submission;
        this.criteria = criteria;
        this.scoreValue = scoreValue;
        this.reviewDate = LocalDateTime.now();
    }

}
