package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Track;

import java.util.List;

public class TrackRepository extends AbstractRepository<Track, Long> {
    public TrackRepository(EntityManager entityManager, Class<Track> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Track> findByEventId(Long eventId){
        return entityManager
                .createQuery("select t from track t where t.event.id=:eventId", Track.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }
}
