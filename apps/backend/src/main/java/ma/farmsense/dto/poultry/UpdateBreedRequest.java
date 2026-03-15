package ma.farmsense.dto.poultry;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import ma.farmsense.entity.BreedCategory;
import ma.farmsense.entity.BreedPurpose;

import java.math.BigDecimal;
import java.util.List;

public class UpdateBreedRequest {
    @Size(max = 255)
    private String name;
    
    @Size(max = 255)
    private String nameAr;
    
    @Size(max = 255)
    private String nameEn;
    
    private BreedCategory category;
    private BreedPurpose purpose;
    private String origin;
    private String description;
    private String descriptionAr;
    private String descriptionEn;
    private String imageUrl;
    private String climateSuitability;
    private BigDecimal avgWeightMaleKg;
    private BigDecimal avgWeightFemaleKg;
    
    @Valid
    private List<FeedingProgramTemplateDto> feedingPrograms;
    
    @Valid
    private List<VaccinationTemplateDto> vaccinationSchedule;
    
    @Valid
    private List<ProductionBenchmarkDto> productionBenchmarks;
    
    @Valid
    private List<HousingGuidelineDto> housingGuidelines;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public BreedCategory getCategory() { return category; }
    public void setCategory(BreedCategory category) { this.category = category; }
    public BreedPurpose getPurpose() { return purpose; }
    public void setPurpose(BreedPurpose purpose) { this.purpose = purpose; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDescriptionAr() { return descriptionAr; }
    public void setDescriptionAr(String descriptionAr) { this.descriptionAr = descriptionAr; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getClimateSuitability() { return climateSuitability; }
    public void setClimateSuitability(String climateSuitability) { this.climateSuitability = climateSuitability; }
    public BigDecimal getAvgWeightMaleKg() { return avgWeightMaleKg; }
    public void setAvgWeightMaleKg(BigDecimal avgWeightMaleKg) { this.avgWeightMaleKg = avgWeightMaleKg; }
    public BigDecimal getAvgWeightFemaleKg() { return avgWeightFemaleKg; }
    public void setAvgWeightFemaleKg(BigDecimal avgWeightFemaleKg) { this.avgWeightFemaleKg = avgWeightFemaleKg; }
    public List<FeedingProgramTemplateDto> getFeedingPrograms() { return feedingPrograms; }
    public void setFeedingPrograms(List<FeedingProgramTemplateDto> feedingPrograms) { this.feedingPrograms = feedingPrograms; }
    public List<VaccinationTemplateDto> getVaccinationSchedule() { return vaccinationSchedule; }
    public void setVaccinationSchedule(List<VaccinationTemplateDto> vaccinationSchedule) { this.vaccinationSchedule = vaccinationSchedule; }
    public List<ProductionBenchmarkDto> getProductionBenchmarks() { return productionBenchmarks; }
    public void setProductionBenchmarks(List<ProductionBenchmarkDto> productionBenchmarks) { this.productionBenchmarks = productionBenchmarks; }
    public List<HousingGuidelineDto> getHousingGuidelines() { return housingGuidelines; }
    public void setHousingGuidelines(List<HousingGuidelineDto> housingGuidelines) { this.housingGuidelines = housingGuidelines; }
}
