package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CropPlanService {

    private final CropPlanRepository cropPlanRepository;
    private final PlantingRepository plantingRepository;
    private final FarmLocationRepository farmLocationRepository;
    private final PlantingNoteRepository plantingNoteRepository;
    private final CropRepository cropRepository;

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
        CropPlan plan = CropPlan.builder()
                .user(user)
                .name(req.getName())
                .nameAr(req.getNameAr())
                .nameEn(req.getNameEn())
                .description(req.getDescription())
                .descriptionAr(req.getDescriptionAr())
                .descriptionEn(req.getDescriptionEn())
                .season(req.getSeason())
                .year(req.getYear())
                .build();
        return CropPlanResponse.from(cropPlanRepository.save(plan), 0);
    }

    @Transactional
    public CropPlanResponse updatePlan(User user, UUID id, UpdateCropPlanRequest req) {
        CropPlan plan = getOwnedPlan(user, id);
        if (req.getName() != null) plan.setName(req.getName());
        if (req.getNameAr() != null) plan.setNameAr(req.getNameAr());
        if (req.getNameEn() != null) plan.setNameEn(req.getNameEn());
        if (req.getDescription() != null) plan.setDescription(req.getDescription());
        if (req.getDescriptionAr() != null) plan.setDescriptionAr(req.getDescriptionAr());
        if (req.getDescriptionEn() != null) plan.setDescriptionEn(req.getDescriptionEn());
        if (req.getSeason() != null) plan.setSeason(req.getSeason());
        if (req.getYear() != null) plan.setYear(req.getYear());
        if (req.getStatus() != null) plan.setStatus(req.getStatus());
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
        FarmLocation location = FarmLocation.builder()
                .user(user)
                .name(req.getName())
                .nameAr(req.getNameAr())
                .nameEn(req.getNameEn())
                .description(req.getDescription())
                .descriptionAr(req.getDescriptionAr())
                .descriptionEn(req.getDescriptionEn())
                .locationType(req.getLocationType())
                .areaM2(req.getAreaM2())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .build();
        return LocationResponse.from(farmLocationRepository.save(location));
    }

    @Transactional
    public LocationResponse updateLocation(User user, UUID id, UpdateLocationRequest req) {
        FarmLocation location = getOwnedLocation(user, id);
        if (req.getName() != null) location.setName(req.getName());
        if (req.getNameAr() != null) location.setNameAr(req.getNameAr());
        if (req.getNameEn() != null) location.setNameEn(req.getNameEn());
        if (req.getDescription() != null) location.setDescription(req.getDescription());
        if (req.getDescriptionAr() != null) location.setDescriptionAr(req.getDescriptionAr());
        if (req.getDescriptionEn() != null) location.setDescriptionEn(req.getDescriptionEn());
        if (req.getLocationType() != null) location.setLocationType(req.getLocationType());
        if (req.getAreaM2() != null) location.setAreaM2(req.getAreaM2());
        if (req.getLatitude() != null) location.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) location.setLongitude(req.getLongitude());
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
        Crop crop = cropRepository.findById(req.getCropId())
                .orElseThrow(() -> AppException.notFound("Crop not found"));

        FarmLocation location = null;
        if (req.getFarmLocationId() != null) {
            location = getOwnedLocation(user, req.getFarmLocationId());
        }

        LocalDate plannedHarvest = req.getPlannedHarvestDate();
        if (plannedHarvest == null && req.getPlannedSowDate() != null && crop.getDaysToHarvest() != null) {
            plannedHarvest = req.getPlannedSowDate().plusDays(crop.getDaysToHarvest());
        }

        Planting planting = Planting.builder()
                .cropPlan(plan)
                .crop(crop)
                .farmLocation(location)
                .quantity(req.getQuantity())
                .areaM2(req.getAreaM2())
                .notes(req.getNotes())
                .plannedSowDate(req.getPlannedSowDate())
                .plannedTransplantDate(req.getPlannedTransplantDate())
                .plannedHarvestDate(plannedHarvest)
                .build();
        return PlantingResponse.from(plantingRepository.save(planting));
    }

    @Transactional
    public PlantingResponse updatePlanting(User user, UUID planId, UUID plantingId, UpdatePlantingRequest req) {
        Planting planting = getOwnedPlanting(user, planId, plantingId);

        if (req.getFarmLocationId() != null) {
            FarmLocation location = getOwnedLocation(user, req.getFarmLocationId());
            planting.setFarmLocation(location);
        }
        if (req.getQuantity() != null) planting.setQuantity(req.getQuantity());
        if (req.getAreaM2() != null) planting.setAreaM2(req.getAreaM2());
        if (req.getNotes() != null) planting.setNotes(req.getNotes());
        if (req.getPlannedSowDate() != null) planting.setPlannedSowDate(req.getPlannedSowDate());
        if (req.getPlannedTransplantDate() != null) planting.setPlannedTransplantDate(req.getPlannedTransplantDate());
        if (req.getPlannedHarvestDate() != null) planting.setPlannedHarvestDate(req.getPlannedHarvestDate());
        if (req.getFailureReason() != null) planting.setFailureReason(req.getFailureReason());

        // Record actual dates on status changes
        if (req.getStatus() != null) {
            planting.setStatus(req.getStatus());
            switch (req.getStatus()) {
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
        planting.setYieldAmount(req.getYieldAmount());
        planting.setYieldUnit(req.getYieldUnit());
        if (req.getQualityRating() != null) planting.setQualityRating(req.getQualityRating());
        if (req.getHarvestNotes() != null) planting.setHarvestNotes(req.getHarvestNotes());
        return PlantingResponse.from(plantingRepository.save(planting));
    }

    // ── Notes ─────────────────────────────────────────────────────

    @Transactional
    public PlantingNoteResponse addNote(User user, UUID planId, UUID plantingId, CreatePlantingNoteRequest req) {
        Planting planting = getOwnedPlanting(user, planId, plantingId);
        PlantingNote note = PlantingNote.builder()
                .planting(planting)
                .content(req.getContent())
                .build();
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
