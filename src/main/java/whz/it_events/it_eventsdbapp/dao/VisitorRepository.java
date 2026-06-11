package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Visitor;

import java.util.List;
import java.util.Optional;

public class VisitorRepository extends AbstractRepository<Visitor, Long> {
    protected VisitorRepository(EntityManager entityManager, Class<Visitor> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Visitor> findBySessionId (Long sessionId){
        return entityManager
                .createQuery("select v from visitor v where v.session.id=:sId", Visitor.class)
                .setParameter("sId", sessionId)
                .getResultList();
    }

    public Optional<Visitor> findByUserAndSession (Long userId, Long sessionId){
        return entityManager
                .createQuery("select v from visitor v where v.user.id=:uId and v.session.id=:sId", Visitor.class)
                .setParameter("uId", userId)
                .setParameter("sId", sessionId)
                .getResultStream().findFirst();
    }
}
