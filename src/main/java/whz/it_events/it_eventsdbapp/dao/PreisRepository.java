package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Preis;

import java.util.List;

public class PreisRepository extends AbstractRepository<Preis, Long> {
    protected PreisRepository(EntityManager entityManager, Class<Preis> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Preis> findByTrackId(Long trackId) {
        return entityManager
                .createQuery("SELECT ps.preis from preis_sponsor ps where ps.track.id = :trackId", Preis.class)
                .setParameter("trackId", trackId)
                .getResultList();
    }

}
