package ma.farmsense.dto.crop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.IssueType;

public record CropIssueRequest(
        @NotNull IssueType issueType,
        @NotBlank String name,
        String nameAr,
        String nameEn,
        String symptoms,
        String symptomsAr,
        String symptomsEn,
        String treatment,
        String treatmentAr,
        String treatmentEn,
        String prevention
) {}
