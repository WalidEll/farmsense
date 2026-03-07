package ma.farmsense.dto.alert;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.Alert;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class AlertResponse {

    private UUID id;
    private UUID plantId;
    private String plantName;
    private String type;
    private String severity;
    private Double sensorValue;
    private String msgFr;
    private String msgAr;
    private String msgDarija;
    private boolean waSent;
    private Instant triggeredAt;
    private Instant ackAt;

    public static AlertResponse from(Alert a) {
        return AlertResponse.builder()
                .id(a.getId())
                .plantId(a.getPlant() != null ? a.getPlant().getId() : null)
                .plantName(a.getPlant() != null ? a.getPlant().getName() : null)
                .type(a.getType().name())
                .severity(a.getSeverity().name())
                .sensorValue(a.getSensorValue())
                .msgFr(a.getMsgFr())
                .msgAr(a.getMsgAr())
                .msgDarija(a.getMsgDarija())
                .waSent(a.isWaSent())
                .triggeredAt(a.getTriggeredAt())
                .ackAt(a.getAckAt())
                .build();
    }
}
