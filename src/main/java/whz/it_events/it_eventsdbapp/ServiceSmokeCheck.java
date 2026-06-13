package whz.it_events.it_eventsdbapp;

import jakarta.persistence.EntityManager;
import whz.it_events.it_eventsdbapp.dao.EventRepository;
import whz.it_events.it_eventsdbapp.dao.ParticipantRepository;
import whz.it_events.it_eventsdbapp.dao.TrackRepository;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.Event;
import whz.it_events.it_eventsdbapp.model.Location;
import whz.it_events.it_eventsdbapp.model.Participant;
import whz.it_events.it_eventsdbapp.model.Track;
import whz.it_events.it_eventsdbapp.model.User;
import whz.it_events.it_eventsdbapp.model.enums.Role;
import whz.it_events.it_eventsdbapp.model.enums.Status;
import whz.it_events.it_eventsdbapp.service.EventService;
import whz.it_events.it_eventsdbapp.service.ServiceException;
import whz.it_events.it_eventsdbapp.service.dto.EventCard;
import whz.it_events.it_eventsdbapp.service.dto.TrackOption;
import whz.it_events.it_eventsdbapp.service.dto.UserHomeData;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ServiceSmokeCheck {
    public static void main(String[] args) {
        SmokeData data = SmokeData.create();
        EventService service = new EventService(
                new InMemoryEventRepository(data),
                new InMemoryTrackRepository(data),
                new InMemoryParticipantRepository(data),
                new InMemoryUserRepository(data)
        );

        System.out.println("=== SERVICE SMOKE CHECK ===");
        printHomeData("Initial home page", service.getHomeData(data.userId));

        System.out.println();
        System.out.println("Registering user for track " + data.backendTrackId + "...");
        service.registerForTrack(data.userId, data.backendTrackId);
        printHomeData("After registration", service.getHomeData(data.userId));

        System.out.println();
        System.out.println("Trying duplicate registration...");
        try {
            service.registerForTrack(data.userId, data.backendTrackId);
            System.out.println("FAIL: duplicate registration was allowed");
        } catch (ServiceException exception) {
            System.out.println("OK: duplicate registration blocked: " + exception.getMessage());
        }

        System.out.println();
        System.out.println("Canceling registration...");
        service.cancelTrackRegistration(data.userId, data.backendTrackId);
        printHomeData("After cancel", service.getHomeData(data.userId));

        System.out.println();
        System.out.println("=== CHECK FINISHED ===");
    }

    private static void printHomeData(String title, UserHomeData homeData) {
        System.out.println("--- " + title + " ---");
        System.out.println("User: " + homeData.userName() + " (id=" + homeData.userId() + ")");
        System.out.println("All events: " + homeData.allEvents().size());
        for (EventCard event : homeData.allEvents()) {
            System.out.println(formatEvent(event));
            for (TrackOption track : event.tracks()) {
                System.out.println("    track: " + track.name() + ", registered=" + track.registered());
            }
        }
        System.out.println("Registered events: " + homeData.registeredEvents().size());
        for (EventCard event : homeData.registeredEvents()) {
            System.out.println("  * " + event.name());
        }
    }

    private static String formatEvent(EventCard event) {
        return "  [" + event.id() + "] "
                + event.name()
                + " | " + event.status()
                + " | " + event.locationName()
                + ", " + event.city()
                + " | registered=" + event.registered();
    }

    private static final class SmokeData {
        private final Long userId = 1L;
        private final Long backendTrackId = 101L;
        private final List<User> users = new ArrayList<>();
        private final List<Event> events = new ArrayList<>();
        private final List<Track> tracks = new ArrayList<>();
        private final List<Participant> participants = new ArrayList<>();

        static SmokeData create() {
            SmokeData data = new SmokeData();

            User user = new User("Aida", "Test", "aida@example.com", Role.USER, "password");
            set(user, "id", data.userId);

            Location location = new Location();
            set(location, "id", 1L);
            set(location, "locationName", "WHZ Campus");
            set(location, "stadt", "Zwickau");
            set(location, "address", "Scheffelstrasse 39");

            Event event = new Event(
                    "IT Hackathon",
                    "Build a useful prototype in a team.",
                    LocalDateTime.now().plusDays(10),
                    LocalDateTime.now().plusDays(11),
                    location
            );
            set(event, "id", 10L);
            set(event, "status", Status.GEPLANT);

            Track backend = new Track();
            set(backend, "id", data.backendTrackId);
            set(backend, "name", "Backend");
            set(backend, "description", "APIs and database logic");
            set(backend, "deadlineDate", LocalDateTime.now().plusDays(5));
            set(backend, "event", event);

            Track frontend = new Track();
            set(frontend, "id", 102L);
            set(frontend, "name", "Frontend");
            set(frontend, "description", "JavaFX UI");
            set(frontend, "deadlineDate", LocalDateTime.now().plusDays(5));
            set(frontend, "event", event);

            set(event, "tracks", List.of(backend, frontend));

            data.users.add(user);
            data.events.add(event);
            data.tracks.add(backend);
            data.tracks.add(frontend);

            return data;
        }
    }

    private static final class InMemoryEventRepository extends EventRepository {
        private final SmokeData data;

        private InMemoryEventRepository(SmokeData data) {
            super((EntityManager) null, Event.class);
            this.data = data;
        }

        @Override
        public Optional<Event> findById(Long id) {
            return data.events.stream()
                    .filter(event -> Objects.equals(event.getId(), id))
                    .findFirst();
        }

        @Override
        public List<Event> findAllOrderedByStartDate() {
            return data.events.stream()
                    .sorted(Comparator.comparing(Event::getStartDate))
                    .toList();
        }

        @Override
        public List<Event> findRegisteredByUserId(Long userId) {
            return data.participants.stream()
                    .filter(participant -> Objects.equals(getField(participant, "user", User.class).getId(), userId))
                    .map(Participant::getTrack)
                    .map(Track::getEvent)
                    .distinct()
                    .sorted(Comparator.comparing(Event::getStartDate))
                    .toList();
        }
    }

    private static final class InMemoryTrackRepository extends TrackRepository {
        private final SmokeData data;

        private InMemoryTrackRepository(SmokeData data) {
            super((EntityManager) null, Track.class);
            this.data = data;
        }

        @Override
        public Optional<Track> findById(Long id) {
            return data.tracks.stream()
                    .filter(track -> Objects.equals(track.getId(), id))
                    .findFirst();
        }

        @Override
        public List<Track> findByEventId(Long eventId) {
            return data.tracks.stream()
                    .filter(track -> Objects.equals(track.getEvent().getId(), eventId))
                    .toList();
        }
    }

    private static final class InMemoryParticipantRepository extends ParticipantRepository {
        private final SmokeData data;

        private InMemoryParticipantRepository(SmokeData data) {
            super((EntityManager) null, Participant.class);
            this.data = data;
        }

        @Override
        public Participant save(Participant participant) {
            set(participant, "id", (long) data.participants.size() + 1);
            data.participants.add(participant);
            return participant;
        }

        @Override
        public void delete(Participant participant) {
            data.participants.remove(participant);
        }

        @Override
        public List<Participant> findByUserId(Long userId) {
            return data.participants.stream()
                    .filter(participant -> Objects.equals(getField(participant, "user", User.class).getId(), userId))
                    .toList();
        }

        @Override
        public Optional<Participant> findByUserIdAndTrackId(Long userId, Long trackId) {
            return data.participants.stream()
                    .filter(participant -> Objects.equals(getField(participant, "user", User.class).getId(), userId))
                    .filter(participant -> Objects.equals(participant.getTrack().getId(), trackId))
                    .findFirst();
        }

        @Override
        public boolean isAlreadyRegistered(Long userId, Long trackId) {
            return findByUserIdAndTrackId(userId, trackId).isPresent();
        }
    }

    private static final class InMemoryUserRepository extends UserRepository {
        private final SmokeData data;

        private InMemoryUserRepository(SmokeData data) {
            super((EntityManager) null, User.class);
            this.data = data;
        }

        @Override
        public Optional<User> findById(Long id) {
            return data.users.stream()
                    .filter(user -> Objects.equals(user.getId(), id))
                    .findFirst();
        }
    }

    private static void set(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot set field " + fieldName, exception);
        }
    }

    private static <T> T getField(Object target, String fieldName, Class<T> type) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return type.cast(field.get(target));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot read field " + fieldName, exception);
        }
    }
}
