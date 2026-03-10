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
                .name(req.getName())
                .nameAr(req.getNameAr())
                .nameEn(req.getNameEn())
                .scientificName(req.getScientificName())
                .category(req.getCategory())
                .description(req.getDescription())
                .descriptionAr(req.getDescriptionAr())
                .descriptionEn(req.getDescriptionEn())
                .imageUrl(req.getImageUrl())
                .growingSeason(req.getGrowingSeason())
                .daysToHarvest(req.getDaysToHarvest())
                .difficulty(req.getDifficulty())
                .build();
        return CropResponse.from(cropRepository.save(crop));
    }

    @Transactional
    public CropResponse update(UUID id, UpdateCropRequest req) {
        Crop crop = getCrop(id);
        if (req.getName() != null) crop.setName(req.getName());
        if (req.getNameAr() != null) crop.setNameAr(req.getNameAr());
        if (req.getNameEn() != null) crop.setNameEn(req.getNameEn());
        if (req.getScientificName() != null) crop.setScientificName(req.getScientificName());
        if (req.getCategory() != null) crop.setCategory(req.getCategory());
        if (req.getDescription() != null) crop.setDescription(req.getDescription());
        if (req.getDescriptionAr() != null) crop.setDescriptionAr(req.getDescriptionAr());
        if (req.getDescriptionEn() != null) crop.setDescriptionEn(req.getDescriptionEn());
        if (req.getImageUrl() != null) crop.setImageUrl(req.getImageUrl());
        if (req.getGrowingSeason() != null) crop.setGrowingSeason(req.getGrowingSeason());
        if (req.getDaysToHarvest() != null) crop.setDaysToHarvest(req.getDaysToHarvest());
        if (req.getDifficulty() != null) crop.setDifficulty(req.getDifficulty());
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
        if (req.getSoilMoistureMin() != null) r.setSoilMoistureMin(req.getSoilMoistureMin());
        if (req.getSoilMoistureMax() != null) r.setSoilMoistureMax(req.getSoilMoistureMax());
        if (req.getTempMin() != null) r.setTempMin(req.getTempMin());
        if (req.getTempMax() != null) r.setTempMax(req.getTempMax());
        if (req.getLightMin() != null) r.setLightMin(req.getLightMin());
        if (req.getLightMax() != null) r.setLightMax(req.getLightMax());
        if (req.getHumidityMin() != null) r.setHumidityMin(req.getHumidityMin());
        if (req.getHumidityMax() != null) r.setHumidityMax(req.getHumidityMax());
        if (req.getSoilType() != null) r.setSoilType(req.getSoilType());
        if (req.getPhMin() != null) r.setPhMin(req.getPhMin());
        if (req.getPhMax() != null) r.setPhMax(req.getPhMax());
        if (req.getWaterFrequency() != null) r.setWaterFrequency(req.getWaterFrequency());
        return CropRequirementResponse.from(requirementRepository.save(r));
    }

    // ── Growth Stages ────────────────────────────────────────────

    @Transactional
    public CropGrowthStageResponse addStage(UUID cropId, CropGrowthStageRequest req) {
        Crop crop = getCrop(cropId);
        CropGrowthStage s = CropGrowthStage.builder()
                .crop(crop)
                .stageOrder(req.getStageOrder())
                .name(req.getName())
                .nameAr(req.getNameAr())
                .nameEn(req.getNameEn())
                .durationDays(req.getDurationDays())
                .description(req.getDescription())
                .descriptionAr(req.getDescriptionAr())
                .descriptionEn(req.getDescriptionEn())
                .build();
        return CropGrowthStageResponse.from(stageRepository.save(s));
    }

    @Transactional
    public CropGrowthStageResponse updateStage(UUID cropId, UUID stageId, CropGrowthStageRequest req) {
        getCrop(cropId);
        CropGrowthStage s = stageRepository.findById(stageId)
                .orElseThrow(() -> AppException.notFound("Growth stage not found"));
        if (req.getStageOrder() != null) s.setStageOrder(req.getStageOrder());
        if (req.getName() != null) s.setName(req.getName());
        if (req.getNameAr() != null) s.setNameAr(req.getNameAr());
        if (req.getNameEn() != null) s.setNameEn(req.getNameEn());
        if (req.getDurationDays() != null) s.setDurationDays(req.getDurationDays());
        if (req.getDescription() != null) s.setDescription(req.getDescription());
        if (req.getDescriptionAr() != null) s.setDescriptionAr(req.getDescriptionAr());
        if (req.getDescriptionEn() != null) s.setDescriptionEn(req.getDescriptionEn());
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
        if (req.getNitrogenNeed() != null) n.setNitrogenNeed(req.getNitrogenNeed());
        if (req.getPhosphorusNeed() != null) n.setPhosphorusNeed(req.getPhosphorusNeed());
        if (req.getPotassiumNeed() != null) n.setPotassiumNeed(req.getPotassiumNeed());
        if (req.getFertilizerType() != null) n.setFertilizerType(req.getFertilizerType());
        if (req.getFertilizerTypeAr() != null) n.setFertilizerTypeAr(req.getFertilizerTypeAr());
        if (req.getFertilizerTypeEn() != null) n.setFertilizerTypeEn(req.getFertilizerTypeEn());
        if (req.getApplicationFrequency() != null) n.setApplicationFrequency(req.getApplicationFrequency());
        return CropNutrientResponse.from(nutrientRepository.save(n));
    }

    // ── Issues ───────────────────────────────────────────────────

    @Transactional
    public CropIssueResponse addIssue(UUID cropId, CropIssueRequest req) {
        Crop crop = getCrop(cropId);
        CropIssue i = CropIssue.builder()
                .crop(crop)
                .issueType(req.getIssueType())
                .name(req.getName())
                .nameAr(req.getNameAr())
                .nameEn(req.getNameEn())
                .symptoms(req.getSymptoms())
                .symptomsAr(req.getSymptomsAr())
                .symptomsEn(req.getSymptomsEn())
                .treatment(req.getTreatment())
                .treatmentAr(req.getTreatmentAr())
                .treatmentEn(req.getTreatmentEn())
                .prevention(req.getPrevention())
                .build();
        return CropIssueResponse.from(issueRepository.save(i));
    }

    @Transactional
    public CropIssueResponse updateIssue(UUID cropId, UUID issueId, CropIssueRequest req) {
        getCrop(cropId);
        CropIssue i = issueRepository.findById(issueId)
                .orElseThrow(() -> AppException.notFound("Issue not found"));
        if (req.getIssueType() != null) i.setIssueType(req.getIssueType());
        if (req.getName() != null) i.setName(req.getName());
        if (req.getNameAr() != null) i.setNameAr(req.getNameAr());
        if (req.getNameEn() != null) i.setNameEn(req.getNameEn());
        if (req.getSymptoms() != null) i.setSymptoms(req.getSymptoms());
        if (req.getSymptomsAr() != null) i.setSymptomsAr(req.getSymptomsAr());
        if (req.getSymptomsEn() != null) i.setSymptomsEn(req.getSymptomsEn());
        if (req.getTreatment() != null) i.setTreatment(req.getTreatment());
        if (req.getTreatmentAr() != null) i.setTreatmentAr(req.getTreatmentAr());
        if (req.getTreatmentEn() != null) i.setTreatmentEn(req.getTreatmentEn());
        if (req.getPrevention() != null) i.setPrevention(req.getPrevention());
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
