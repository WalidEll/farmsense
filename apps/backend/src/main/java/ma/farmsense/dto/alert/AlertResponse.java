package ma.farmsense.dto.alert;

import ma.farmsense.entity.Alert;

import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        UUID plantId,
        String plantName,
        String type,
        String severity,
        Double sensorValue,
        String msgFr,
        String msgAr,
        String msgEn,
        boolean waSent,
        Instant triggeredAt,
        Instant ackAt
) {
    public static AlertResponse from(Alert a) {
        return new AlertResponse(
                a.getId(),
                a.getPlant() != null ? a.getPlant().getId() : null,
                a.getPlant() != null ? a.getPlant().getName() : null,
                a.getType().name(),
                a.getSeverity().name(),
                a.getSensorValue(),
                a.getMsgFr(),
                a.getMsgAr(),
                a.getMsgEn(),
                a.isWaSent(),
                a.getTriggeredAt(),
                a.getAckAt()
        );
    }
}
