package whz.it_events.it_eventsdbapp.model.enums;

public enum ParticipationType {
    TEAM("team"),
    SINGLE("single");

    private final String value;

    ParticipationType(String value) {
        this.value = value;
    }
}
