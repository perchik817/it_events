package whz.it_events.it_eventsdbapp.service.dto;

import java.util.List;

public record UserHomeData(
        Long userId,
        String userName,
        List<EventCard> allEvents,
        List<EventCard> registeredEvents
) {
}
