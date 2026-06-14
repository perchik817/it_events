package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "member")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    @Column(name = "team_role")
    String teamRole;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    public Member(Team team, String teamRole, User user) {
        this.team = team;
        this.teamRole = teamRole;
        this.user = user;
    }
}
