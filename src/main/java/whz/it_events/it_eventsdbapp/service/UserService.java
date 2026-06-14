package whz.it_events.it_eventsdbapp.service;

import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.User;

import java.util.Optional;

public record UserService(UserRepository repo) {

    public void save(User user) {
        repo.save(user);
    }

    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }
}
