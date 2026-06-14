package whz.it_events.it_eventsdbapp.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SubmissionStatusConverter implements AttributeConverter<SubmissionStatus, String> {

    @Override
    public String convertToDatabaseColumn(SubmissionStatus attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case EINGERICHTET -> "eingereicht";
            case PRÜFT -> "prüft";
            case BEWERTET -> "bewertet";
            case NOMINIERT -> "nominiert";
            case GEWONNEN -> "gewonnen";
            case DISQUALIFIZIERT -> "disqualifiziert";
        };
    }

    @Override
    public SubmissionStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData.toLowerCase()) {
            case "eingereicht" -> SubmissionStatus.EINGERICHTET;
            case "prüft" -> SubmissionStatus.PRÜFT;
            case "bewertet" -> SubmissionStatus.BEWERTET;
            case "nominiert" -> SubmissionStatus.NOMINIERT;
            case "gewonnen" -> SubmissionStatus.GEWONNEN;
            case "disqualifiziert" -> SubmissionStatus.DISQUALIFIZIERT;
            default -> throw new IllegalArgumentException("Unknown status: " + dbData);
        };
    }
}
