package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Session;
import whz.it_events.it_eventsdbapp.model.enums.SessionType;

import java.util.List;

public class SessionRepository extends AbstractRepository<Session, Long> {
    public SessionRepository(EntityManager entityManager, Class<Session> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Session> findByEventId(Long eventId){
        return entityManager
                .createQuery("select s from session s where s.event.id=:eventId", Session.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    public List<Session> findByType(SessionType type){
        return entityManager
                .createQuery("select s from session s where s.sessionType=:type", Session.class)
                .setParameter("type", type)
                .getResultList();
    }

    /**Frei Plaetze*/
    public int countVisitors(Long sessionId){
        Integer count = entityManager
                .createQuery("select count(v) from visitor v where v.session.id=:sessionId", Integer.class)
                .setParameter("sessionId", sessionId)
                .getSingleResult();
        return count.intValue();
    }
}
