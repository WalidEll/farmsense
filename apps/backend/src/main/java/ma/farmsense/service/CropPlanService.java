package ma.farmsense.service;

import ma.farmsense.dto.cropplan.*;
import ma.farmsense.entity.*;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CropPlanService {

    private final CropPlanRepository cropPlanRepository;
    private final PlantingRepository plantingRepository;
    private final FarmLocationRepository farmLocationRepository;
    private final PlantingNoteRepository plantingNoteRepository;
    private final CropRepository cropRepository;

    public CropPlanService(CropPlanRepository cropPlanRepository, PlantingRepository plantingRepository,
                           FarmLocationRepository farmLocationRepository, PlantingNoteRepository plantingNoteRepository,
                           CropRepository cropRepository) {
        this.cropPlanRepository = cropPlanRepository;
        this.plantingRepository = plantingRepository;
        this.farmLocationRepository = farmLocationRepository;
        this.plantingNoteRepository = plantingNoteRepository;
        this.cropRepository = cropRepository;
    }

    // ── Crop Plans ────────────────────────────────────────────────

    public List<CropPlanResponse> findAllPlans(User user) {
        return cropPlanRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(p -> CropPlanResponse.from(p, plantingRepository.countByCropPlan(p)))
                .toList();
    }

    public CropPlanResponse findPlanById(User user, UUID id) {
        CropPlan plan = getOwnedPlan(user, id);
        long count = plantingRepository.countByCropPlan(plan);
        return CropPlanResponse.from(plan, count);
    }

    @Transactional
    public CropPlanResponse createPlan(User user, CreateCropPlanRequest req) {
        CropPlan plan = new CropPlan();
        plan.setUser(user);
        plan.setName(req.name());
        plan.setNameAr(req.nameAr());
        plan.setNameEn(req.nameEn());
        plan.setDescription(req.description());
        plan.setDescriptionAr(req.descriptionAr());
        plan.setDescriptionEn(req.descriptionEn());
        plan.setSeason(req.season());
        plan.setYear(req.year());
        return CropPlanResponse.from(cropPlanRepository.save(plan), 0);
    }

    @Transactional
    public CropPlanResponse updatePlan(User user, UUID id, UpdateCropPlanRequest req) {
        CropPlan plan = getOwnedPlan(user, id);
        if (req.name() != null) plan.setName(req.name());
        if (req.nameAr() != null) plan.setNameAr(req.nameAr());
        if (req.nameEn() != null) plan.setNameEn(req.nameEn());
        if (req.description() != null) plan.setDescription(req.description());
        if (req.descriptionAr() != null) plan.setDescriptionAr(req.descriptionAr());
        if (req.descriptionEn() != null) plan.setDescriptionEn(req.descriptionEn());
        if (req.season() != null) plan.setSeason(req.season());
        if (req.year() != null) plan.setYear(req.year());
        if (req.status() != null) plan.setStatus(req.status());
        CropPlan saved = cropPlanRepository.save(plan);
        long count = plantingRepository.countByCropPlan(saved);
        return CropPlanResponse.from(saved, count);
    }

    @Transactional
    public void deletePlan(User user, UUID id) {
        CropPlan plan = getOwnedPlan(user, id);
        if (plan.getStatus() == PlanStatus.DRAFT) {
            cropPlanRepository.delete(plan);
        } else {
            plan.setStatus(PlanStatus.ARCHIVED);
            cropPlanRepository.save(plan);
        }
    }

    // ── Farm Locations ────────────────────────────────────────────

    public List<LocationResponse> findAllLocations(User user) {
        return farmLocationRepository.findByUserOrderByNameAsc(user).stream()
                .map(LocationResponse::from)
                .toList();
    }

    @Transactional
    public LocationResponse createLocation(User user, CreateLocationRequest req) {
        FarmLocation location = new FarmLocation();
        location.setUser(user);
        location.setName(req.name());
        location.setNameAr(req.nameAr());
        location.setNameEn(req.nameEn());
        location.setDescription(req.description());
        location.setDescriptionAr(req.descriptionAr());
        location.setDescriptionEn(req.descriptionEn());
        location.setLocationType(req.locationType());
        location.setAreaM2(req.areaM2());
        location.setLatitude(req.latitude());
        location.setLongitude(req.longitude());
        return LocationResponse.from(farmLocationRepository.save(location));
    }

    @Transactional
    public LocationResponse updateLocation(User user, UUID id, UpdateLocationRequest req) {
        FarmLocation location = getOwnedLocation(user, id);
        if (req.name() != null) location.setName(req.name());
        if (req.nameAr() != null) location.setNameAr(req.nameAr());
        if (req.nameEn() != null) location.setNameEn(req.nameEn());
        if (req.description() != null) location.setDescription(req.description());
        if (req.descriptionAr() != null) location.setDescriptionAr(req.descriptionAr());
        if (req.descriptionEn() != null) location.setDescriptionEn(req.descriptionEn());
        if (req.locationType() != null) location.setLocationType(req.locationType());
        if (req.areaM2() != null) location.setAreaM2(req.areaM2());
        if (req.latitude() != null) location.setLatitude(req.latitude());
        if (req.longitude() != null) location.setLongitude(req.longitude());
        return LocationResponse.from(farmLocationRepository.save(location));
    }

    @Transactional
    public void deleteLocation(User user, UUID id) {
        FarmLocation location = getOwnedLocation(user, id);
        long activePlantings = plantingRepository.countByFarmLocationAndStatusNotIn(
                location, List.of(PlantingStatus.COMPLETED, PlantingStatus.FAILED));
        if (activePlantings > 0) {
            throw AppException.conflict("Cannot delete location with active plantings");
        }
        farmLocationRepository.delete(location);
    }

    // ── Plantings ─────────────────────────────────────────────────

    public List<PlantingResponse> findPlantings(User user, UUID planId) {
        CropPlan plan = getOwnedPlan(user, planId);
        return plantingRepository.findByCropPlanOrderByPlannedSowDateAsc(plan).stream()
                .map(PlantingResponse::from)
                .toList();
    }

    @Transactional
    public PlantingResponse createPlanting(User user, UUID planId, CreatePlantingRequest req) {
        CropPlan plan = getOwnedPlan(user, planId);
        Crop crop = cropRepository.findById(req.cropId())
                .orElseThrow(() -> AppException.notFound("Crop not found"));

        FarmLocation location = null;
        if (req.farmLocationId() != null) {
            location = getOwnedLocation(user, req.farmLocationId());
        }

        LocalDate plannedHarvest = req.plannedHarvestDate();
        if (plannedHarvest == null && req.plannedSowDate() != null && crop.getDaysToHarvest() != null) {
            plannedHarvest = req.plannedSowDate().plusDays(crop.getDaysToHarvest());
        }

        Planting planting = new Planting();
        planting.setCropPlan(plan);
        planting.setCrop(crop);
        planting.setFarmLocation(location);
        planting.setQuantity(req.quantity());
        planting.setAreaM2(req.areaM2());
        planting.setNotes(req.notes());
        planting.setPlannedSowDate(req.plannedSowDate());
        planting.setPlannedTransplantDate(req.plannedTransplantDate());
        planting.setPlannedHarvestDate(plannedHarvest);
        return PlantingResponse.from(plantingRepository.save(planting));
    }

    @Transactional
    public PlantingResponse updatePlanting(User user, UUID planId, UUID plantingId, UpdatePlantingRequest req) {
        Planting planting = getOwnedPlanting(user, planId, plantingId);

        if (req.farmLocationId() != null) {
            FarmLocation location = getOwnedLocation(user, req.farmLocationId());
            planting.setFarmLocation(location);
        }
        if (req.quantity() != null) planting.setQuantity(req.quantity());
        if (req.areaM2() != null) planting.setAreaM2(req.areaM2());
        if (req.notes() != null) planting.setNotes(req.notes());
        if (req.plannedSowDate() != null) planting.setPlannedSowDate(req.plannedSowDate());
        if (req.plannedTransplantDate() != null) planting.setPlannedTransplantDate(req.plannedTransplantDate());
        if (req.plannedHarvestDate() != null) planting.setPlannedHarvestDate(req.plannedHarvestDate());
        if (req.failureReason() != null) planting.setFailureReason(req.failureReason());

        // Record actual dates on status changes
        if (req.status() != null) {
            planting.setStatus(req.status());
            switch (req.status()) {
                case SOWN -> planting.setActualSowDate(LocalDate.now());
                case TRANSPLANTED -> planting.setActualTransplantDate(LocalDate.now());
                case HARVESTING, COMPLETED -> planting.setActualHarvestDate(LocalDate.now());
                default -> { }
            }
        }

        return PlantingResponse.from(plantingRepository.save(planting));
    }

    @Transactional
    public void deletePlanting(User user, UUID planId, UUID plantingId) {
        Planting planting = getOwnedPlanting(user, planId, plantingId);
        plantingRepository.delete(planting);
    }

    // ── Yield ─────────────────────────────────────────────────────

    @Transactional
    public PlantingResponse setYield(User user, UUID planId, UUID plantingId, PlantingYieldRequest req) {
        Planting planting = getOwnedPlanting(user, planId, plantingId);
        planting.setYieldAmount(req.yieldAmount());
        planting.setYieldUnit(req.yieldUnit());
        if (req.qualityRating() != null) planting.setQualityRating(req.qualityRating());
        if (req.harvestNotes() != null) planting.setHarvestNotes(req.harvestNotes());
        return PlantingResponse.from(plantingRepository.save(planting));
    }

    // ── Notes ─────────────────────────────────────────────────────

    @Transactional
    public PlantingNoteResponse addNote(User user, UUID planId, UUID plantingId, CreatePlantingNoteRequest req) {
        Planting planting = getOwnedPlanting(user, planId, plantingId);
        PlantingNote note = new PlantingNote();
        note.setPlanting(planting);
        note.setContent(req.content());
        return PlantingNoteResponse.from(plantingNoteRepository.save(note));
    }

    public List<PlantingNoteResponse> getNotes(User user, UUID planId, UUID plantingId) {
        Planting planting = getOwnedPlanting(user, planId, plantingId);
        return plantingNoteRepository.findByPlantingOrderByCreatedAtDesc(planting).stream()
                .map(PlantingNoteResponse::from)
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────

    public CropPlan getOwnedPlan(User user, UUID planId) {
        CropPlan plan = cropPlanRepository.findById(planId)
                .orElseThrow(() -> AppException.notFound("Crop plan not found"));
        if (!plan.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Crop plan not found");
        }
        return plan;
    }

    public FarmLocation getOwnedLocation(User user, UUID locationId) {
        FarmLocation location = farmLocationRepository.findById(locationId)
                .orElseThrow(() -> AppException.notFound("Farm location not found"));
        if (!location.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Farm location not found");
        }
        return location;
    }

    private Planting getOwnedPlanting(User user, UUID planId, UUID plantingId) {
        CropPlan plan = getOwnedPlan(user, planId);
        Planting planting = plantingRepository.findById(plantingId)
                .orElseThrow(() -> AppException.notFound("Planting not found"));
        if (!planting.getCropPlan().getId().equals(plan.getId())) {
            throw AppException.notFound("Planting not found");
        }
        return planting;
    }
}
