package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.User;

import java.util.Optional;

public class UserRepository extends AbstractRepository<User, Long> {
    public UserRepository(EntityManager entityManager, Class<User> entityClass) {
        super(entityManager, entityClass);
    }

    public Optional<User> findByEmail(String email){
        return entityManager
                .createQuery("select u from user u where u.email=:email", User.class)
                .setParameter("email", email)
                .getResultStream().findFirst();
    }

    public boolean existsByEmail(String email){
        Long count = entityManager
                .createQuery("select count(u) from user u where u.email=:email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
