package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import whz.it_events.it_eventsdbapp.model.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "appUser")
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(name = "last_name")
    String lastname;
    String email;
    @Enumerated(EnumType.STRING)
    Role role;
    String password;
    @Column(name="registration_date")
    LocalDateTime registrationDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Participant> participants;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Jury> juries;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Mentor> mentors;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Visitor> visitors;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Organisator> organisators;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Member> members;

    public User(String name, String lastname, String email, Role role, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.password = password;
        this.registrationDate = LocalDateTime.now();
    }
}
