package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Team;

import java.util.List;

public class TeamRepository extends AbstractRepository<Team, Long> {
    public TeamRepository(EntityManager entityManager, Class<Team> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Team> findByTrackId(Long trackId){
        return entityManager
                .createQuery("select t from team t where t.track.id=:tId", Team.class)
                .setParameter("tId", trackId)
                .getResultList();
    }

    /**Top-N Team durch scoreValue in jedem Track
     * limit = max count von Top-Teams*/
    public List<Team> findTopByTrack(Long trackId, int limit){
        return entityManager
                .createQuery("select t from track t where t.track.id=:tId order by t.scoreValue desc", Team.class)
                .setParameter("tId", trackId)
                .setMaxResults(limit)
                .getResultList();
    }
}
