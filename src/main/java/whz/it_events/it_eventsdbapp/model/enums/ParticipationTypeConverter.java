package whz.it_events.it_eventsdbapp.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ParticipationTypeConverter implements AttributeConverter<ParticipationType, String> {

    @Override
    public String convertToDatabaseColumn(ParticipationType attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case TEAM -> "team";
            case SINGLE -> "single";
        };
    }

    @Override
    public ParticipationType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "team" -> ParticipationType.TEAM;
            case "single" -> ParticipationType.SINGLE;
            default -> throw new IllegalArgumentException("Unknown type: " + dbData);
        };
    }
}
