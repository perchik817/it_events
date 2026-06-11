package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Participant;

import java.util.List;
import java.util.Optional;

public class ParticipantRepository extends AbstractRepository<Participant, Long> {
    protected ParticipantRepository(EntityManager entityManager, Class<Participant> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Participant> findByTrackId(Long trackId){
        return entityManager
                .createQuery("select p from participant p where p.track.id=:tId", Participant.class)
                .setParameter("tId", trackId)
                .getResultList();
    }

    public List<Participant> findByUserId(Long userId){
        return entityManager
                .createQuery("select p from participant p where p.user.id=:uId", Participant.class)
                .setParameter("uId", userId)
                .getResultList();
    }

    public Optional<Participant> findByUserIdAndTrackId(Long userId, Long trackId){
        return  entityManager
                .createQuery("select p from participant p where p.user.id=:uId and p.track.id=:tId", Participant.class)
                .setParameter("uId", userId)
                .setParameter("tId", trackId)
                .getResultStream().findFirst();
    }

    public boolean isAlreadyRegistered(Long userId, Long trackId){
        return findByUserIdAndTrackId(userId, trackId).isPresent();
    }
}
