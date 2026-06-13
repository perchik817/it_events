package whz.it_events.it_eventsdbapp.service;

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
import whz.it_events.it_eventsdbapp.model.enums.Status;
import whz.it_events.it_eventsdbapp.service.dto.EventCard;
import whz.it_events.it_eventsdbapp.service.dto.TrackOption;
import whz.it_events.it_eventsdbapp.service.dto.UserHomeData;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EventService {
    private final EventRepository eventRepository;
    private final TrackRepository trackRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public EventService(EntityManager entityManager) {
        this(
                new EventRepository(entityManager, Event.class),
                new TrackRepository(entityManager, Track.class),
                new ParticipantRepository(entityManager, Participant.class),
                new UserRepository(entityManager, User.class)
        );
    }

    public EventService(
            EventRepository eventRepository,
            TrackRepository trackRepository,
            ParticipantRepository participantRepository,
            UserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.trackRepository = trackRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    public UserHomeData getHomeData(Long userId) {
        User user = findUser(userId);
        List<EventCard> allEvents = getAllEventsForUser(userId);
        List<EventCard> registeredEvents = getRegisteredEvents(userId);
        return new UserHomeData(user.getId(), fullName(user), allEvents, registeredEvents);
    }

    public List<EventCard> getAllEventsForUser(Long userId) {
        Set<Long> registeredTrackIds = findRegisteredTrackIds(userId);
        return eventRepository.findAllOrderedByStartDate()
                .stream()
                .map(event -> toEventCard(event, registeredTrackIds))
                .toList();
    }

    public List<EventCard> getRegisteredEvents(Long userId) {
        findUser(userId);
        Set<Long> registeredTrackIds = findRegisteredTrackIds(userId);
        return eventRepository.findRegisteredByUserId(userId)
                .stream()
                .map(event -> toEventCard(event, registeredTrackIds))
                .toList();
    }

    public EventCard getEvent(Long eventId, Long userId) {
        Event event = findEvent(eventId);
        return toEventCard(event, findRegisteredTrackIds(userId));
    }

    public Participant registerForEvent(Long userId, Long eventId) {
        findUser(userId);
        Event event = findEvent(eventId);
        List<Track> tracks = loadTracks(event);

        if (tracks.isEmpty()) {
            throw new ServiceException("This event has no tracks available for registration.");
        }
        if (tracks.size() > 1) {
            throw new ServiceException("Please choose a track before registering for this event.");
        }
        return registerForTrack(userId, tracks.getFirst().getId());
    }

    public Participant registerForTrack(Long userId, Long trackId) {
        User user = findUser(userId);
        Track track = findTrack(trackId);
        validateRegistration(track, userId);

        Participant participant = new Participant();
        participant.setUser(user);
        participant.setTrack(track);
        return participantRepository.save(participant);
    }

    public void cancelTrackRegistration(Long userId, Long trackId) {
        findUser(userId);
        Participant participant = participantRepository.findByUserIdAndTrackId(userId, trackId)
                .orElseThrow(() -> new ServiceException("Registration was not found."));
        participantRepository.delete(participant);
    }

    public boolean isRegisteredForEvent(Long userId, Long eventId) {
        findUser(userId);
        Event event = findEvent(eventId);
        Set<Long> registeredTrackIds = findRegisteredTrackIds(userId);
        return loadTracks(event).stream()
                .map(Track::getId)
                .anyMatch(registeredTrackIds::contains);
    }

    private void validateRegistration(Track track, Long userId) {
        Event event = track.getEvent();
        if (event == null) {
            throw new ServiceException("Track is not connected to an event.");
        }
        if (event.getStatus() == Status.ABGESAGT || event.getStatus() == Status.ABGESCHLOSSEN) {
            throw new ServiceException("Registration for this event is closed.");
        }
        if (track.getDeadlineDate() != null && track.getDeadlineDate().isBefore(LocalDateTime.now())) {
            throw new ServiceException("Registration deadline for this track has passed.");
        }
        if (participantRepository.isAlreadyRegistered(userId, track.getId())) {
            throw new ServiceException("User is already registered for this track.");
        }
    }

    private EventCard toEventCard(Event event, Set<Long> registeredTrackIds) {
        Location location = event.getLocation();
        List<TrackOption> tracks = loadTracks(event).stream()
                .map(track -> new TrackOption(
                        track.getId(),
                        track.getName(),
                        track.getDescription(),
                        track.getDeadlineDate(),
                        registeredTrackIds.contains(track.getId())
                ))
                .toList();

        boolean registered = tracks.stream().anyMatch(TrackOption::registered);

        return new EventCard(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                location != null ? location.getLocationName() : null,
                location != null ? location.getStadt() : null,
                location != null ? location.getAddress() : null,
                event.getStatus(),
                tracks,
                registered
        );
    }

    private List<Track> loadTracks(Event event) {
        if (event.getTracks() != null) {
            return event.getTracks();
        }
        if (event.getId() == null) {
            return Collections.emptyList();
        }
        return trackRepository.findByEventId(event.getId());
    }

    private Set<Long> findRegisteredTrackIds(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        List<Participant> participants = participantRepository.findByUserId(userId);
        Set<Long> ids = new HashSet<>();
        for (Participant participant : participants) {
            if (participant.getTrack() != null && participant.getTrack().getId() != null) {
                ids.add(participant.getTrack().getId());
            }
        }
        return ids;
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(Objects.requireNonNull(eventId, "eventId"))
                .orElseThrow(() -> new ServiceException("Event was not found."));
    }

    private Track findTrack(Long trackId) {
        return trackRepository.findById(Objects.requireNonNull(trackId, "trackId"))
                .orElseThrow(() -> new ServiceException("Track was not found."));
    }

    private User findUser(Long userId) {
        return userRepository.findById(Objects.requireNonNull(userId, "userId"))
                .orElseThrow(() -> new ServiceException("User was not found."));
    }

    private String fullName(User user) {
        return String.join(" ",
                user.getName() != null ? user.getName() : "",
                user.getLastname() != null ? user.getLastname() : ""
        ).trim();
    }
}
