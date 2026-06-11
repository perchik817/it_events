package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Event;
import whz.it_events.it_eventsdbapp.model.enums.Status;

import java.util.List;

public class EventRepository extends AbstractRepository<Event, Long> {
    protected EventRepository(EntityManager entityManager, Class<Event> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Event> findByStatus(Status status){
        return entityManager
                .createQuery("select e from event e where e.status=:status", Event.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Event> findByLocationId(Long locationId){
        return entityManager
                .createQuery("select e from event e where e.location.id=:locationId", Event.class)
                .setParameter("locationId", locationId)
                .getResultList();
    }

    public List<Event> findByNameContaining(String keyword) {
        return entityManager.createQuery("select e from event e where lower(e.name) like lower(:kw)", Event.class)
                .setParameter("kw", "%" + keyword + "%")
                .getResultList();
    }

}
