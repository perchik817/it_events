package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.PreisSponsor;

public class PreisSponsorRepository extends AbstractRepository<PreisSponsor, Long> {
    public PreisSponsorRepository(EntityManager entityManager, Class<PreisSponsor> entityClass) {
        super(entityManager, entityClass);
    }
}
