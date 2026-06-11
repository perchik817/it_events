package whz.it_events.it_eventsdbapp.base;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {
    T save(T entity);
    void delete (T entity);
    void deleteById(Long id);
    Optional<T> findById (Long id);
    List<T> findAll ();

}
