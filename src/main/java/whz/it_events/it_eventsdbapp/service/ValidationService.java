package whz.it_events.it_eventsdbapp.service;

public class ValidationService {

    public void validateRegistration(String name, String lastname, String email, String password) {

        if (name == null || name.isBlank())
            throw new RuntimeException("Vorname erforderlich");

        if (lastname == null || lastname.isBlank())
            throw new RuntimeException("Name erforderlich");

        if (email == null || !email.contains("@"))
            throw new RuntimeException("Ungültige E-Mail-Adresse");

        if (password == null || password.length() < 4)
            throw new RuntimeException("Das Passwort ist zu kurz");
    }

    public void validateLogin(String email, String password) {
        if (email == null || email.isBlank())
            throw new RuntimeException("Email erforderlich");

        if (password == null || password.isBlank())
            throw new RuntimeException("Passwort erforderlich");
    }
}