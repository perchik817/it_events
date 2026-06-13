package whz.it_events.it_eventsdbapp.service.dto;

import whz.it_events.it_eventsdbapp.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public record EventCard(
        Long id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String locationName,
        String city,
        String address,
        Status status,
        List<TrackOption> tracks,
        boolean registered
) {
}
