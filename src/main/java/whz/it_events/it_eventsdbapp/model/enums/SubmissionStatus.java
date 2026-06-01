package whz.it_events.it_eventsdbapp.model.enums;

public enum SubmissionStatus {
    EINGERICHTET("eingereicht"),
    PRÜFT("prüft"),
    BEWERTET("bewertet"),
    NOMINIERT("nominiert"),
    GEWONNEN("gewonnen"),
    DISQUALIFIZIERT("disqualifiziert");

    private final String value;

    SubmissionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
