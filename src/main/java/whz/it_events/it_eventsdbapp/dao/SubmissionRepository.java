package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Submission;
import whz.it_events.it_eventsdbapp.model.enums.SubmissionStatus;

import java.util.List;

public class SubmissionRepository extends AbstractRepository<Submission, Long> {
    protected SubmissionRepository(EntityManager entityManager, Class<Submission> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Submission> findByStatus(SubmissionStatus status){
        return entityManager
                .createQuery("select s from submission s where s.status=:status", Submission.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Submission> findByParticipantUserId(Long userId){
        return entityManager
                .createQuery("select distinct p.submission from participant p " +
                        "where p.user.id=:uId and p.submission is not null", Submission.class)
                .setParameter("uId", userId)
                .getResultList();
    }
}
