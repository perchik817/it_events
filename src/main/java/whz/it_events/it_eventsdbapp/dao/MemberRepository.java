package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Member;

import java.util.List;

public class MemberRepository extends AbstractRepository<Member, Long> {
    protected MemberRepository(EntityManager entityManager, Class<Member> entityClass) {
        super(entityManager, entityClass);
    }

    public List<Member> findByTeamName(String teamName){
        return entityManager
                .createQuery("select m from member m where m.team.name=:tName", Member.class)
                .setParameter("tName", teamName)
                .getResultList();
    }

    public List<Member> findByTeamId(Long teamId){
        return entityManager
                .createQuery("select m from member m where m.team.id=:tId", Member.class)
                .setParameter("tId", teamId)
                .getResultList();
    }

    public List<Member> findByRole(String role) {
        return entityManager
                .createQuery("select m from member m where m.teamRole = :role", Member.class)
                .setParameter("role", role)
                .getResultList();
    }
}
