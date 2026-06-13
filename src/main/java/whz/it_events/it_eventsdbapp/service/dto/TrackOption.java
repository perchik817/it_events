package whz.it_events.it_eventsdbapp.service.dto;

import java.time.LocalDateTime;

public record TrackOption(
        Long id,
        String name,
        String description,
        LocalDateTime deadlineDate,
        boolean registered
) {
}
