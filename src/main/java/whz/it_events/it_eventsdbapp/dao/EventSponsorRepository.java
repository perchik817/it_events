package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.EventSponsor;

public class EventSponsorRepository extends AbstractRepository<EventSponsor, Long> {
    public EventSponsorRepository(EntityManager entityManager, Class<EventSponsor> entityClass) {
        super(entityManager, entityClass);
    }
}
