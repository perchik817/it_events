package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Speaker;

import java.util.List;

public class SpeakerRepository extends AbstractRepository<Speaker, Long> {
    public SpeakerRepository(EntityManager entityManager, Class<Speaker> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Speaker> findBySessionId(Long sessionId) {
        return entityManager
                .createQuery("select ss.speaker from session_speaker ss where ss.session.id=:sId", Speaker.class)
                .setParameter("sId", sessionId)
                .getResultList();
    }
}