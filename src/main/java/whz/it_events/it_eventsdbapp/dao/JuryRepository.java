package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Jury;

import java.util.List;
import java.util.Optional;

public class JuryRepository extends AbstractRepository<Jury, Long> {
    protected JuryRepository(EntityManager entityManager, Class<Jury> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Jury> findByTrackId(Long trackId){
        return entityManager
                .createQuery("select j from jury j where j.track.id=:tId", Jury.class)
                .setParameter("tId", trackId)
                .getResultList();
    }

    public Optional<Jury> findByUserAndTrack(Long userId, Long trackId){
        return entityManager
                .createQuery("select j from jury j where j.user.id=:uId and j.track.id=:tId", Jury.class)
                .setParameter("uId", userId)
                .setParameter("tId", trackId)
                .getResultStream().findFirst();
    }
}
