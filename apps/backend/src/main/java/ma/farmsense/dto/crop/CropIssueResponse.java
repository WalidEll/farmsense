package ma.farmsense.dto.crop;

import ma.farmsense.entity.CropIssue;
import ma.farmsense.entity.IssueType;

import java.util.UUID;

public record CropIssueResponse(
        UUID id,
        IssueType issueType,
        String name,
        String nameAr,
        String nameEn,
        String symptoms,
        String symptomsAr,
        String symptomsEn,
        String treatment,
        String treatmentAr,
        String treatmentEn,
        String prevention
) {
    public static CropIssueResponse from(CropIssue i) {
        return new CropIssueResponse(
                i.getId(),
                i.getIssueType(),
                i.getName(),
                i.getNameAr(),
                i.getNameEn(),
                i.getSymptoms(),
                i.getSymptomsAr(),
                i.getSymptomsEn(),
                i.getTreatment(),
                i.getTreatmentAr(),
                i.getTreatmentEn(),
                i.getPrevention()
        );
    }
}
