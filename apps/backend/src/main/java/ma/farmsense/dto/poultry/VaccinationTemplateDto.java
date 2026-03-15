package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.AdministrationRoute;
import ma.farmsense.entity.VaccinationTemplate;

import java.util.UUID;

public record VaccinationTemplateDto(
        UUID id,
        @NotBlank String vaccineName,
        @NotNull @Min(1) Integer recommendedAgeDays,
        String dosage,
        @NotNull AdministrationRoute administrationRoute,
        Boolean isMandatory,
        String notes,
        Integer sortOrder
) {
    public static VaccinationTemplateDto from(VaccinationTemplate template) {
        return new VaccinationTemplateDto(
                template.getId(),
                template.getVaccineName(),
                template.getRecommendedAgeDays(),
                template.getDosage(),
                template.getAdministrationRoute(),
                template.getIsMandatory(),
                template.getNotes(),
                template.getSortOrder()
        );
    }
}
