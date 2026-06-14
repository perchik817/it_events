package whz.it_events.it_eventsdbapp.service;

import org.mindrot.jbcrypt.BCrypt;
import whz.it_events.it_eventsdbapp.model.User;
import whz.it_events.it_eventsdbapp.model.enums.Role;

public record RegisterService(UserService userService, ValidationService validationService) {

    public User register(String name, String lastname, String email, String password) {

        validationService.validateRegistration(name, lastname, email, password);
        if (userService.existsByEmail(email)) {
            throw new RuntimeException("Diese E-Mail-Adresse ist bereits vorhanden");
        }
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(
                name,
                lastname,
                email,
                Role.USER,
                hashed
        );
        userService.save(user);
        return user;
    }
}
