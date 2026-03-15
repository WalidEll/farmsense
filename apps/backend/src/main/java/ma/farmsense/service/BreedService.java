package ma.farmsense.service;

import ma.farmsense.dto.poultry.*;
import ma.farmsense.entity.*;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.BreedRepository;
import ma.farmsense.repository.FlockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BreedService {

    private final BreedRepository breedRepository;
    private final FlockRepository flockRepository;

    public BreedService(BreedRepository breedRepository, FlockRepository flockRepository) {
        this.breedRepository = breedRepository;
        this.flockRepository = flockRepository;
    }

    /**
     * Retrieves a paginated list of breeds with optional search and filtering.
     *
     * @param user    the authenticated user
     * @param search  optional search term for name
     * @param category optional breed category filter
     * @param purpose optional breed purpose filter
     * @param page    page number (0-indexed)
     * @param size    page size
     * @return paginated list of breed summaries
     */
    public Page<BreedSummaryResponse> findAll(User user, String search, BreedCategory category, 
                                               BreedPurpose purpose, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        
        Page<Breed> breedsPage;
        
        if (search != null && !search.isBlank()) {
            if (category != null && purpose != null) {
                breedsPage = breedRepository.searchByCategoryAndPurpose(search, category, purpose, user, pageable);
            } else if (category != null) {
                breedsPage = breedRepository.searchByCategory(search, category, user, pageable);
            } else if (purpose != null) {
                breedsPage = breedRepository.searchByPurpose(search, purpose, user, pageable);
            } else {
                breedsPage = breedRepository.search(search, user, pageable);
            }
        } else {
            if (category != null && purpose != null) {
                breedsPage = breedRepository.findByCategoryAndPurpose(category, purpose, user, pageable);
            } else if (category != null) {
                breedsPage = breedRepository.findByCategory(category, user, pageable);
            } else if (purpose != null) {
                breedsPage = breedRepository.findByPurpose(purpose, user, pageable);
            } else {
                breedsPage = breedRepository.findByIsSystemTrueOrUserOrderByNameAsc(user, pageable);
            }
        }
        
        return breedsPage.map(BreedSummaryResponse::from);
    }

    /**
     * Retrieves detailed information about a specific breed.
     *
     * @param user the authenticated user
     * @param id   the breed UUID
     * @return detailed breed response including all templates
     */
    public BreedDetailResponse findById(User user, UUID id) {
        Breed breed = getVisible(user, id);
        return BreedDetailResponse.from(breed);
    }

    @Transactional
    public BreedDetailResponse create(User user, CreateBreedRequest req) {
        if (breedRepository.countByUserAndNameIgnoreCase(user, req.getName()) > 0) {
            throw AppException.conflict("Breed with this name already exists");
        }

        if (req.getCategory() != BreedCategory.CUSTOM) {
            throw AppException.badRequest("Only CUSTOM category breeds can be created");
        }

        Breed breed = new Breed();
        breed.setUser(user);
        breed.setName(req.getName());
        breed.setNameAr(req.getNameAr());
        breed.setNameEn(req.getNameEn());
        breed.setCategory(BreedCategory.CUSTOM);
        breed.setPurpose(req.getPurpose());
        breed.setOrigin(req.getOrigin());
        breed.setDescription(req.getDescription());
        breed.setDescriptionAr(req.getDescriptionAr());
        breed.setDescriptionEn(req.getDescriptionEn());
        breed.setImageUrl(req.getImageUrl());
        breed.setClimateSuitability(req.getClimateSuitability());
        breed.setAvgWeightMaleKg(req.getAvgWeightMaleKg());
        breed.setAvgWeightFemaleKg(req.getAvgWeightFemaleKg());
        breed.setIsSystem(false);

        if (req.getFeedingPrograms() != null) {
            List<FeedingProgramTemplate> templates = new ArrayList<>();
            for (FeedingProgramTemplateDto dto : req.getFeedingPrograms()) {
                FeedingProgramTemplate t = new FeedingProgramTemplate();
                t.setBreed(breed);
                t.setGrowthStage(dto.growthStage());
                t.setAgeStartDays(dto.ageStartDays());
                t.setAgeEndDays(dto.ageEndDays());
                t.setFeedType(dto.feedType());
                t.setDailyQuantityGrams(dto.dailyQuantityGrams());
                t.setFeedingFrequency(dto.feedingFrequency());
                t.setNotes(dto.notes());
                t.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
                templates.add(t);
            }
            breed.setFeedingPrograms(templates);
        }

        if (req.getVaccinationSchedule() != null) {
            List<VaccinationTemplate> templates = new ArrayList<>();
            for (VaccinationTemplateDto dto : req.getVaccinationSchedule()) {
                VaccinationTemplate t = new VaccinationTemplate();
                t.setBreed(breed);
                t.setVaccineName(dto.vaccineName());
                t.setRecommendedAgeDays(dto.recommendedAgeDays());
                t.setDosage(dto.dosage());
                t.setAdministrationRoute(dto.administrationRoute());
                t.setIsMandatory(dto.isMandatory() != null ? dto.isMandatory() : true);
                t.setNotes(dto.notes());
                t.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
                templates.add(t);
            }
            breed.setVaccinationSchedule(templates);
        }

        if (req.getProductionBenchmarks() != null) {
            List<ProductionBenchmark> benchmarks = new ArrayList<>();
            for (ProductionBenchmarkDto dto : req.getProductionBenchmarks()) {
                ProductionBenchmark b = new ProductionBenchmark();
                b.setBreed(breed);
                b.setMetricType(dto.metricType());
                b.setExpectedValue(dto.expectedValue());
                b.setUnit(dto.unit());
                b.setAgeStartDays(dto.ageStartDays());
                b.setAgeEndDays(dto.ageEndDays());
                b.setNotes(dto.notes());
                b.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
                benchmarks.add(b);
            }
            breed.setProductionBenchmarks(benchmarks);
        }

        if (req.getHousingGuidelines() != null) {
            List<HousingGuideline> guidelines = new ArrayList<>();
            for (HousingGuidelineDto dto : req.getHousingGuidelines()) {
                HousingGuideline g = new HousingGuideline();
                g.setBreed(breed);
                g.setParameterName(dto.parameterName());
                g.setRecommendedValue(dto.recommendedValue());
                g.setUnit(dto.unit());
                g.setGrowthStage(dto.growthStage());
                g.setNotes(dto.notes());
                g.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
                guidelines.add(g);
            }
            breed.setHousingGuidelines(guidelines);
        }

        return BreedDetailResponse.from(breedRepository.save(breed));
    }

    /**
     * Updates an existing custom breed and its templates.
     *
     * @param user the authenticated user (must own the breed)
     * @param id   the breed UUID
     * @param req  the update breed request
     * @return the updated breed detail response
     */
    @Transactional
    public BreedDetailResponse update(User user, UUID id, UpdateBreedRequest req) {
        Breed breed = getOwned(user, id);

        if (req.getName() != null) breed.setName(req.getName());
        if (req.getNameAr() != null) breed.setNameAr(req.getNameAr());
        if (req.getNameEn() != null) breed.setNameEn(req.getNameEn());
        if (req.getCategory() != null) breed.setCategory(req.getCategory());
        if (req.getPurpose() != null) breed.setPurpose(req.getPurpose());
        if (req.getOrigin() != null) breed.setOrigin(req.getOrigin());
        if (req.getDescription() != null) breed.setDescription(req.getDescription());
        if (req.getDescriptionAr() != null) breed.setDescriptionAr(req.getDescriptionAr());
        if (req.getDescriptionEn() != null) breed.setDescriptionEn(req.getDescriptionEn());
        if (req.getImageUrl() != null) breed.setImageUrl(req.getImageUrl());
        if (req.getClimateSuitability() != null) breed.setClimateSuitability(req.getClimateSuitability());
        if (req.getAvgWeightMaleKg() != null) breed.setAvgWeightMaleKg(req.getAvgWeightMaleKg());
        if (req.getAvgWeightFemaleKg() != null) breed.setAvgWeightFemaleKg(req.getAvgWeightFemaleKg());

        if (req.getFeedingPrograms() != null) {
            breed.getFeedingPrograms().clear();
            for (FeedingProgramTemplateDto dto : req.getFeedingPrograms()) {
                FeedingProgramTemplate t = new FeedingProgramTemplate();
                t.setBreed(breed);
                t.setGrowthStage(dto.growthStage());
                t.setAgeStartDays(dto.ageStartDays());
                t.setAgeEndDays(dto.ageEndDays());
                t.setFeedType(dto.feedType());
                t.setDailyQuantityGrams(dto.dailyQuantityGrams());
                t.setFeedingFrequency(dto.feedingFrequency());
                t.setNotes(dto.notes());
                t.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
                breed.getFeedingPrograms().add(t);
            }
        }

        if (req.getVaccinationSchedule() != null) {
            breed.getVaccinationSchedule().clear();
            for (VaccinationTemplateDto dto : req.getVaccinationSchedule()) {
                VaccinationTemplate t = new VaccinationTemplate();
                t.setBreed(breed);
                t.setVaccineName(dto.vaccineName());
                t.setRecommendedAgeDays(dto.recommendedAgeDays());
                t.setDosage(dto.dosage());
                t.setAdministrationRoute(dto.administrationRoute());
                t.setIsMandatory(dto.isMandatory() != null ? dto.isMandatory() : true);
                t.setNotes(dto.notes());
                t.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
                breed.getVaccinationSchedule().add(t);
            }
        }

        if (req.getProductionBenchmarks() != null) {
            breed.getProductionBenchmarks().clear();
            for (ProductionBenchmarkDto dto : req.getProductionBenchmarks()) {
                ProductionBenchmark b = new ProductionBenchmark();
                b.setBreed(breed);
                b.setMetricType(dto.metricType());
                b.setExpectedValue(dto.expectedValue());
                b.setUnit(dto.unit());
                b.setAgeStartDays(dto.ageStartDays());
                b.setAgeEndDays(dto.ageEndDays());
                b.setNotes(dto.notes());
                b.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
                breed.getProductionBenchmarks().add(b);
            }
        }

        if (req.getHousingGuidelines() != null) {
            breed.getHousingGuidelines().clear();
            for (HousingGuidelineDto dto : req.getHousingGuidelines()) {
                HousingGuideline g = new HousingGuideline();
                g.setBreed(breed);
                g.setParameterName(dto.parameterName());
                g.setRecommendedValue(dto.recommendedValue());
                g.setUnit(dto.unit());
                g.setGrowthStage(dto.growthStage());
                g.setNotes(dto.notes());
                g.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
                breed.getHousingGuidelines().add(g);
            }
        }

        return BreedDetailResponse.from(breedRepository.save(breed));
    }

    /**
     * Deletes a custom breed owned by the user.
     *
     * @param user    the authenticated user
     * @param id      the breed UUID
     * @param confirm must be true if breed has active flocks
     * @throws AppException if breed is assigned to active flocks without confirmation
     */
    @Transactional
    public void delete(User user, UUID id, boolean confirm) {
        Breed breed = getOwned(user, id);
        
        long activeFlockCount = flockRepository.countByBreedIdAndStatus(id, FlockStatus.ACTIVE);
        
        if (activeFlockCount > 0 && !confirm) {
            throw AppException.conflict("This breed is assigned to " + activeFlockCount + " active flock(s). Pass ?confirm=true to proceed.");
        }
        
        breedRepository.delete(breed);
    }

    /**
     * Retrieves breed templates (feeding, vaccination, benchmarks, housing).
     *
     * @param user the authenticated user
     * @param id   the breed UUID
     * @return detailed breed response with all templates
     */
    public BreedDetailResponse getTemplates(User user, UUID id) {
        Breed breed = getVisible(user, id);
        return BreedDetailResponse.from(breed);
    }

    /**
     * Gets a breed visible to the user (system breeds or user's own breeds).
     *
     * @param user the authenticated user
     * @param id   the breed UUID
     * @return the breed entity
     * @throws AppException if breed not found
     */
    public Breed getVisible(User user, UUID id) {
        return breedRepository.findByIdAndVisible(id, user)
                .orElseThrow(() -> AppException.notFound("Breed not found"));
    }

    /**
     * Gets a breed owned by the user (only user's own custom breeds).
     *
     * @param user the authenticated user
     * @param id   the breed UUID
     * @return the breed entity
     * @throws AppException if breed not found or not owned by user
     */
    public Breed getOwned(User user, UUID id) {
        return breedRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> AppException.notFound("Breed not found"));
    }
}
