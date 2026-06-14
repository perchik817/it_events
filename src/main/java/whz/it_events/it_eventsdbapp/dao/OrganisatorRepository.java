package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Organisator;

import java.util.List;

public class OrganisatorRepository extends AbstractRepository<Organisator, Long> {
    public OrganisatorRepository(EntityManager entityManager, Class<Organisator> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Organisator> findByEventId(Long eventId) {
        return entityManager
                .createQuery("select o from organisator o where o.event.id = :eid", Organisator.class)
                .setParameter("eid", eventId)
                .getResultList();
    }

}
