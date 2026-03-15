package ma.farmsense.dto.diagnose;

import ma.farmsense.entity.Diagnosis;

import java.time.Instant;
import java.util.UUID;

public record DiagnoseResponse(
        UUID id,
        UUID plantId,
        String plantName,
        String problemName,
        String severity,
        String treatmentFr,
        String treatmentAr,
        String treatmentEn,
        String prevention,
        String rawResponse,
        String photoUrl,
        String shareUrl,
        Instant createdAt
) {
    public static DiagnoseResponse from(Diagnosis d) {
        return new DiagnoseResponse(
                d.getId(),
                d.getPlant() != null ? d.getPlant().getId() : null,
                d.getPlant() != null ? d.getPlant().getName() : null,
                d.getProblemName(),
                d.getSeverity(),
                d.getTreatmentFr(),
                d.getTreatmentAr(),
                d.getTreatmentEn(),
                d.getPrevention(),
                d.getRawResponse(),
                d.getPhotoUrl(),
                null,
                d.getCreatedAt()
        );
    }
}
