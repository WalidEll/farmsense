package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.plant.CreatePlantRequest;
import ma.farmsense.dto.plant.PlantResponse;
import ma.farmsense.dto.plant.UpdatePlantRequest;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.PlantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlantService {

    private final PlantRepository plantRepository;
    private final CareService careService;

    public List<PlantResponse> findAll(User user) {
        return plantRepository.findByUserAndDeletedAtIsNull(user)
                .stream().map(PlantResponse::from).toList();
    }

    @Transactional
    public PlantResponse create(User user, CreatePlantRequest req) {
        Plant.PlantBuilder builder = Plant.builder()
                .user(user)
                .name(req.name())
                .species(req.species())
                .location(req.location())
                .photoUrl(req.photoUrl());

        if (req.soilMin()  != null) builder.soilMin(req.soilMin());
        if (req.soilMax()  != null) builder.soilMax(req.soilMax());
        if (req.tempMin()  != null) builder.tempMin(req.tempMin());
        if (req.tempMax()  != null) builder.tempMax(req.tempMax());
        if (req.lightMin() != null) builder.lightMin(req.lightMin());

        Plant plant = plantRepository.save(builder.build());
        careService.initSchedule(plant);
        return PlantResponse.from(plant);
    }

    @Transactional
    public PlantResponse update(User user, UUID id, UpdatePlantRequest req) {
        Plant plant = getOwned(user, id);

        if (req.name()     != null) plant.setName(req.name());
        if (req.species()  != null) plant.setSpecies(req.species());
        if (req.location() != null) plant.setLocation(req.location());
        if (req.photoUrl() != null) plant.setPhotoUrl(req.photoUrl());
        if (req.soilMin()  != null) plant.setSoilMin(req.soilMin());
        if (req.soilMax()  != null) plant.setSoilMax(req.soilMax());
        if (req.tempMin()  != null) plant.setTempMin(req.tempMin());
        if (req.tempMax()  != null) plant.setTempMax(req.tempMax());
        if (req.lightMin() != null) plant.setLightMin(req.lightMin());

        return PlantResponse.from(plantRepository.save(plant));
    }

    @Transactional
    public void softDelete(User user, UUID id) {
        Plant plant = getOwned(user, id);
        plant.setDeletedAt(Instant.now());
        plantRepository.save(plant);
    }

    public Plant getOwned(User user, UUID id) {
        return plantRepository.findByIdAndUserAndDeletedAtIsNull(id, user)
                .orElseThrow(() -> AppException.notFound("Plant not found"));
    }
}
