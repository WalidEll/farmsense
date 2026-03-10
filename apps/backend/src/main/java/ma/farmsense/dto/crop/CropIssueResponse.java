package ma.farmsense.dto.crop;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.CropIssue;
import ma.farmsense.entity.IssueType;

import java.util.UUID;

@Data
@Builder
public class CropIssueResponse {

    private UUID id;
    private IssueType issueType;
    private String name;
    private String nameAr;
    private String nameDarija;
    private String symptoms;
    private String symptomsAr;
    private String symptomsDarija;
    private String treatment;
    private String treatmentAr;
    private String treatmentDarija;
    private String prevention;

    public static CropIssueResponse from(CropIssue i) {
        return CropIssueResponse.builder()
                .id(i.getId())
                .issueType(i.getIssueType())
                .name(i.getName())
                .nameAr(i.getNameAr())
                .nameDarija(i.getNameDarija())
                .symptoms(i.getSymptoms())
                .symptomsAr(i.getSymptomsAr())
                .symptomsDarija(i.getSymptomsDarija())
                .treatment(i.getTreatment())
                .treatmentAr(i.getTreatmentAr())
                .treatmentDarija(i.getTreatmentDarija())
                .prevention(i.getPrevention())
                .build();
    }
}
