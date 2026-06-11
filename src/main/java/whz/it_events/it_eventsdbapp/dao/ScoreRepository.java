package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Score;

import java.util.List;

public class ScoreRepository extends AbstractRepository<Score, Long> {
    protected ScoreRepository(EntityManager entityManager, Class<Score> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Score> findBySubmissionId(Long submissionId){
        return entityManager
                .createQuery("select s from score s where s.submission.id=:subId", Score.class)
                .setParameter("subId", submissionId)
                .getResultList();
    }

    /**Notendurchschnitt durch Submission*/
    public double calcAverageScore(Long submissionId){
        Double average = entityManager
                .createQuery("select avg(s.scoreValue) from score s where s.submission.id=:subId", Double.class)
                .setParameter("subId", submissionId)
                .getSingleResult();
        return average != null ? average : 0.0;
    }

    /** Notensumme durch Submission*/
    public int calcTotalScore(Long submissionId){
        Integer total = entityManager
                .createQuery("select sum(s.scoreValue) from score s where s.submission.id=:subId", Integer.class)
                .setParameter("subId", submissionId)
                .getSingleResult();
        return total != null ? total : 0;
    }
}
