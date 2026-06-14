package whz.it_events.it_eventsdbapp.dao;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.base.AbstractRepository;
import whz.it_events.it_eventsdbapp.model.Location;

public class LocationRepository extends AbstractRepository<Location, Long> {

    public LocationRepository(EntityManager entityManager, Class<Location> entityClass) {
        super(entityManager, entityClass);
    }
}
