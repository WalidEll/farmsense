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
                .name(req.getName())
                .species(req.getSpecies())
                .location(req.getLocation())
                .photoUrl(req.getPhotoUrl());

        if (req.getSoilMin()  != null) builder.soilMin(req.getSoilMin());
        if (req.getSoilMax()  != null) builder.soilMax(req.getSoilMax());
        if (req.getTempMin()  != null) builder.tempMin(req.getTempMin());
        if (req.getTempMax()  != null) builder.tempMax(req.getTempMax());
        if (req.getLightMin() != null) builder.lightMin(req.getLightMin());

        Plant plant = plantRepository.save(builder.build());
        careService.initSchedule(plant);
        return PlantResponse.from(plant);
    }

    @Transactional
    public PlantResponse update(User user, UUID id, UpdatePlantRequest req) {
        Plant plant = getOwned(user, id);

        if (req.getName()     != null) plant.setName(req.getName());
        if (req.getSpecies()  != null) plant.setSpecies(req.getSpecies());
        if (req.getLocation() != null) plant.setLocation(req.getLocation());
        if (req.getPhotoUrl() != null) plant.setPhotoUrl(req.getPhotoUrl());
        if (req.getSoilMin()  != null) plant.setSoilMin(req.getSoilMin());
        if (req.getSoilMax()  != null) plant.setSoilMax(req.getSoilMax());
        if (req.getTempMin()  != null) plant.setTempMin(req.getTempMin());
        if (req.getTempMax()  != null) plant.setTempMax(req.getTempMax());
        if (req.getLightMin() != null) plant.setLightMin(req.getLightMin());

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
