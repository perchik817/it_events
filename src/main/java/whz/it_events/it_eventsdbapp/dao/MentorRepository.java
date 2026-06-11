package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Mentor;

import java.util.List;

public class MentorRepository extends AbstractRepository<Mentor, Long> {
    protected MentorRepository(EntityManager entityManager, Class<Mentor> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Mentor> findByTrackId(Long trackId){
        return entityManager
                .createQuery("select m from mentor m where m.track.id=:tId", Mentor.class)
                .setParameter("tId", trackId)
                .getResultList();
    }
}
