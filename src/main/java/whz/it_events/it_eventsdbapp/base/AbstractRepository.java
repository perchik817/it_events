package whz.it_events.it_eventsdbapp.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class AbstractRepository<T, ID> implements BaseRepository<T, ID>{
    protected final EntityManager entityManager;
    protected final Class<T> entityClass;

    protected AbstractRepository(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    protected <R> R executeInTransaction(Supplier<R> operation) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            R result = operation.get();
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public T save(T entity) {
        return executeInTransaction(() -> entityManager.merge(entity));
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return entityManager.createQuery(
                "select * from " + entityClass.getSimpleName() + " e",
                entityClass
        ).getResultList();
    }

    @Override
    public void delete(T entity) {
        executeInTransaction(() -> {
            T managedEntity = entityManager.contains(entity) ? entity : entityManager.merge(entity);
            entityManager.remove(managedEntity);
            return null;
        });
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }
}
