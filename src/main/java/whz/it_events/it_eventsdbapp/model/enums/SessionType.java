package whz.it_events.it_eventsdbapp.model.enums;

public enum SessionType {
    ERÖFNUNGSREDE ("eröffnungsrede"), //Keynote
    WORKSHOP("workshop"),
    PITCH("pitch"),
    ZEREMONIE("zeremonie"),
    PAUSE("pause"),
    NETWORKING("networking"),
    PROGRAMMIERTIME("programmiertime"),// HackTime
    PREISVERLEIHUNG("preisverleihung"), // Awarding
    PRÄSENTATION("präsentation"),
    SONSTIGES("sonstiges");
    private final String value;

    SessionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
