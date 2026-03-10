package ma.farmsense.dto.crop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.farmsense.entity.IssueType;

@Data
public class CropIssueRequest {

    @NotNull
    private IssueType issueType;

    @NotBlank
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
}
