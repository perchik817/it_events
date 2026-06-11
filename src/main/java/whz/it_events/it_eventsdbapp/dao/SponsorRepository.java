package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Sponsor;

import java.util.List;

public class SponsorRepository extends AbstractRepository<Sponsor, Long> {
    protected SponsorRepository(EntityManager entityManager, Class<Sponsor> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Sponsor> findByEventId(Long eventId){
        return entityManager
                .createQuery("select es.sponsor from event_sponsor es where es.event.id=:eId", Sponsor.class)
                .setParameter("eId", eventId)
                .getResultList();
    }
}
