package whz.it_events.it_eventsdbapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity (name = "user")
@Data
@FieldDefaults (level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(name = "last_name")
    String lastname;
    String email;
    Role role;
    String password;
    LocalDateTime registrationDate;

    public User(String name, String lastname, String email, Role role, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.password = password;
        this.registrationDate = LocalDateTime.now();
    }
}
