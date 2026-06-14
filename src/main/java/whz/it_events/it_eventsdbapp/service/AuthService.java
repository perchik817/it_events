package whz.it_events.it_eventsdbapp.service;

import org.mindrot.jbcrypt.BCrypt;
import whz.it_events.it_eventsdbapp.model.User;

public record AuthService(UserService userService, ValidationService validationService) {

    public User login(String email, String password) {

        validationService.validateLogin(email, password);
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Falsches Passwort");
        }
        return user;
    }
}
