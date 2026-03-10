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
    private String nameEn;
    private String symptoms;
    private String symptomsAr;
    private String symptomsEn;
    private String treatment;
    private String treatmentAr;
    private String treatmentEn;
    private String prevention;

    public static CropIssueResponse from(CropIssue i) {
        return CropIssueResponse.builder()
                .id(i.getId())
                .issueType(i.getIssueType())
                .name(i.getName())
                .nameAr(i.getNameAr())
                .nameEn(i.getNameEn())
                .symptoms(i.getSymptoms())
                .symptomsAr(i.getSymptomsAr())
                .symptomsEn(i.getSymptomsEn())
                .treatment(i.getTreatment())
                .treatmentAr(i.getTreatmentAr())
                .treatmentEn(i.getTreatmentEn())
                .prevention(i.getPrevention())
                .build();
    }
}
