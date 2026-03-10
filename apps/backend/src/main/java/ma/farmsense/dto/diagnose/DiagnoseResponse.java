package ma.farmsense.dto.diagnose;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.Diagnosis;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class DiagnoseResponse {

    private UUID id;
    private UUID plantId;
    private String plantName;
    private String problemName;
    private String severity;
    private String treatmentFr;
    private String treatmentAr;
    private String treatmentEn;
    private String prevention;
    private String rawResponse;
    private String photoUrl;
    private Instant createdAt;

    public static DiagnoseResponse from(Diagnosis d) {
        return DiagnoseResponse.builder()
                .id(d.getId())
                .plantId(d.getPlant() != null ? d.getPlant().getId() : null)
                .plantName(d.getPlant() != null ? d.getPlant().getName() : null)
                .problemName(d.getProblemName())
                .severity(d.getSeverity())
                .treatmentFr(d.getTreatmentFr())
                .treatmentAr(d.getTreatmentAr())
                .treatmentEn(d.getTreatmentEn())
                .prevention(d.getPrevention())
                .rawResponse(d.getRawResponse())
                .photoUrl(d.getPhotoUrl())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
