package whz.it_events.it_eventsdbapp.model.enums;

public enum Status {
    GEPLANT("geplant"),
    LAUFEND("laufend"),
    ABGESCHLOSSEN("abgeschlossen"),
    ABGESAGT("abgesagt");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
