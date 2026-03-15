package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.crop.*;
import ma.farmsense.entity.*;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CropService {

    private final CropRepository cropRepository;
    private final CropRequirementRepository requirementRepository;
    private final CropGrowthStageRepository stageRepository;
    private final CropNutrientRepository nutrientRepository;
    private final CropIssueRepository issueRepository;

    // ── Crop CRUD ────────────────────────────────────────────────

    public List<CropResponse> findAll(CropCategory category, String search) {
        List<Crop> crops;
        if (category != null && search != null && !search.isBlank()) {
            crops = cropRepository.searchByCategory(category, search);
        } else if (category != null) {
            crops = cropRepository.findByCategory(category);
        } else if (search != null && !search.isBlank()) {
            crops = cropRepository.search(search);
        } else {
            crops = cropRepository.findAll();
        }
        return crops.stream().map(CropResponse::from).toList();
    }

    public CropDetailResponse findById(UUID id) {
        Crop crop = getCrop(id);
        var req = requirementRepository.findByCrop(crop).orElse(null);
        var stages = stageRepository.findByCropOrderByStageOrder(crop);
        var nut = nutrientRepository.findByCrop(crop).orElse(null);
        var issues = issueRepository.findByCrop(crop);
        return CropDetailResponse.from(crop, req, stages, nut, issues);
    }

    @Transactional
    public CropResponse create(CreateCropRequest req) {
        Crop crop = Crop.builder()
                .name(req.name())
                .nameAr(req.nameAr())
                .nameEn(req.nameEn())
                .scientificName(req.scientificName())
                .category(req.category())
                .description(req.description())
                .descriptionAr(req.descriptionAr())
                .descriptionEn(req.descriptionEn())
                .imageUrl(req.imageUrl())
                .growingSeason(req.growingSeason())
                .daysToHarvest(req.daysToHarvest())
                .difficulty(req.difficulty())
                .build();
        return CropResponse.from(cropRepository.save(crop));
    }

    @Transactional
    public CropResponse update(UUID id, UpdateCropRequest req) {
        Crop crop = getCrop(id);
        if (req.name() != null) crop.setName(req.name());
        if (req.nameAr() != null) crop.setNameAr(req.nameAr());
        if (req.nameEn() != null) crop.setNameEn(req.nameEn());
        if (req.scientificName() != null) crop.setScientificName(req.scientificName());
        if (req.category() != null) crop.setCategory(req.category());
        if (req.description() != null) crop.setDescription(req.description());
        if (req.descriptionAr() != null) crop.setDescriptionAr(req.descriptionAr());
        if (req.descriptionEn() != null) crop.setDescriptionEn(req.descriptionEn());
        if (req.imageUrl() != null) crop.setImageUrl(req.imageUrl());
        if (req.growingSeason() != null) crop.setGrowingSeason(req.growingSeason());
        if (req.daysToHarvest() != null) crop.setDaysToHarvest(req.daysToHarvest());
        if (req.difficulty() != null) crop.setDifficulty(req.difficulty());
        return CropResponse.from(cropRepository.save(crop));
    }

    @Transactional
    public void delete(UUID id) {
        Crop crop = getCrop(id);
        cropRepository.delete(crop);
    }

    // ── Requirements ─────────────────────────────────────────────

    @Transactional
    public CropRequirementResponse setRequirements(UUID cropId, CropRequirementRequest req) {
        Crop crop = getCrop(cropId);
        CropRequirement r = requirementRepository.findByCrop(crop)
                .orElse(CropRequirement.builder().crop(crop).build());
        if (req.soilMoistureMin() != null) r.setSoilMoistureMin(req.soilMoistureMin());
        if (req.soilMoistureMax() != null) r.setSoilMoistureMax(req.soilMoistureMax());
        if (req.tempMin() != null) r.setTempMin(req.tempMin());
        if (req.tempMax() != null) r.setTempMax(req.tempMax());
        if (req.lightMin() != null) r.setLightMin(req.lightMin());
        if (req.lightMax() != null) r.setLightMax(req.lightMax());
        if (req.humidityMin() != null) r.setHumidityMin(req.humidityMin());
        if (req.humidityMax() != null) r.setHumidityMax(req.humidityMax());
        if (req.soilType() != null) r.setSoilType(req.soilType());
        if (req.phMin() != null) r.setPhMin(req.phMin());
        if (req.phMax() != null) r.setPhMax(req.phMax());
        if (req.waterFrequency() != null) r.setWaterFrequency(req.waterFrequency());
        return CropRequirementResponse.from(requirementRepository.save(r));
    }

    // ── Growth Stages ────────────────────────────────────────────

    @Transactional
    public CropGrowthStageResponse addStage(UUID cropId, CropGrowthStageRequest req) {
        Crop crop = getCrop(cropId);
        CropGrowthStage s = CropGrowthStage.builder()
                .crop(crop)
                .stageOrder(req.stageOrder())
                .name(req.name())
                .nameAr(req.nameAr())
                .nameEn(req.nameEn())
                .durationDays(req.durationDays())
                .description(req.description())
                .descriptionAr(req.descriptionAr())
                .descriptionEn(req.descriptionEn())
                .build();
        return CropGrowthStageResponse.from(stageRepository.save(s));
    }

    @Transactional
    public CropGrowthStageResponse updateStage(UUID cropId, UUID stageId, CropGrowthStageRequest req) {
        getCrop(cropId);
        CropGrowthStage s = stageRepository.findById(stageId)
                .orElseThrow(() -> AppException.notFound("Growth stage not found"));
        if (req.stageOrder() != null) s.setStageOrder(req.stageOrder());
        if (req.name() != null) s.setName(req.name());
        if (req.nameAr() != null) s.setNameAr(req.nameAr());
        if (req.nameEn() != null) s.setNameEn(req.nameEn());
        if (req.durationDays() != null) s.setDurationDays(req.durationDays());
        if (req.description() != null) s.setDescription(req.description());
        if (req.descriptionAr() != null) s.setDescriptionAr(req.descriptionAr());
        if (req.descriptionEn() != null) s.setDescriptionEn(req.descriptionEn());
        return CropGrowthStageResponse.from(stageRepository.save(s));
    }

    @Transactional
    public void deleteStage(UUID cropId, UUID stageId) {
        getCrop(cropId);
        stageRepository.deleteById(stageId);
    }

    // ── Nutrients ────────────────────────────────────────────────

    @Transactional
    public CropNutrientResponse setNutrients(UUID cropId, CropNutrientRequest req) {
        Crop crop = getCrop(cropId);
        CropNutrient n = nutrientRepository.findByCrop(crop)
                .orElse(CropNutrient.builder().crop(crop).build());
        if (req.nitrogenNeed() != null) n.setNitrogenNeed(req.nitrogenNeed());
        if (req.phosphorusNeed() != null) n.setPhosphorusNeed(req.phosphorusNeed());
        if (req.potassiumNeed() != null) n.setPotassiumNeed(req.potassiumNeed());
        if (req.fertilizerType() != null) n.setFertilizerType(req.fertilizerType());
        if (req.fertilizerTypeAr() != null) n.setFertilizerTypeAr(req.fertilizerTypeAr());
        if (req.fertilizerTypeEn() != null) n.setFertilizerTypeEn(req.fertilizerTypeEn());
        if (req.applicationFrequency() != null) n.setApplicationFrequency(req.applicationFrequency());
        return CropNutrientResponse.from(nutrientRepository.save(n));
    }

    // ── Issues ───────────────────────────────────────────────────

    @Transactional
    public CropIssueResponse addIssue(UUID cropId, CropIssueRequest req) {
        Crop crop = getCrop(cropId);
        CropIssue i = CropIssue.builder()
                .crop(crop)
                .issueType(req.issueType())
                .name(req.name())
                .nameAr(req.nameAr())
                .nameEn(req.nameEn())
                .symptoms(req.symptoms())
                .symptomsAr(req.symptomsAr())
                .symptomsEn(req.symptomsEn())
                .treatment(req.treatment())
                .treatmentAr(req.treatmentAr())
                .treatmentEn(req.treatmentEn())
                .prevention(req.prevention())
                .build();
        return CropIssueResponse.from(issueRepository.save(i));
    }

    @Transactional
    public CropIssueResponse updateIssue(UUID cropId, UUID issueId, CropIssueRequest req) {
        getCrop(cropId);
        CropIssue i = issueRepository.findById(issueId)
                .orElseThrow(() -> AppException.notFound("Issue not found"));
        if (req.issueType() != null) i.setIssueType(req.issueType());
        if (req.name() != null) i.setName(req.name());
        if (req.nameAr() != null) i.setNameAr(req.nameAr());
        if (req.nameEn() != null) i.setNameEn(req.nameEn());
        if (req.symptoms() != null) i.setSymptoms(req.symptoms());
        if (req.symptomsAr() != null) i.setSymptomsAr(req.symptomsAr());
        if (req.symptomsEn() != null) i.setSymptomsEn(req.symptomsEn());
        if (req.treatment() != null) i.setTreatment(req.treatment());
        if (req.treatmentAr() != null) i.setTreatmentAr(req.treatmentAr());
        if (req.treatmentEn() != null) i.setTreatmentEn(req.treatmentEn());
        if (req.prevention() != null) i.setPrevention(req.prevention());
        return CropIssueResponse.from(issueRepository.save(i));
    }

    @Transactional
    public void deleteIssue(UUID cropId, UUID issueId) {
        getCrop(cropId);
        issueRepository.deleteById(issueId);
    }

    // ── Helper ───────────────────────────────────────────────────

    private Crop getCrop(UUID id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Crop not found"));
    }
}
