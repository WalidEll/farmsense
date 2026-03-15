package ma.farmsense.service;

import ma.farmsense.dto.plant.CreatePlantRequest;
import ma.farmsense.dto.plant.PlantResponse;
import ma.farmsense.dto.plant.UpdatePlantRequest;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.PlantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantServiceTest {

    @Mock
    private PlantRepository plantRepository;
    @Mock
    private CareService careService;

    @InjectMocks
    private PlantService plantService;

    @Test
    void findAll_ShouldReturnList_ForUser() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        Plant plant = new Plant();
        plant.setId(UUID.randomUUID());
        plant.setUser(user);
        plant.setName("Lily");
        when(plantRepository.findByUserAndDeletedAtIsNull(user)).thenReturn(List.of(plant));

        // Act
        List<PlantResponse> result = plantService.findAll(user);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Lily", result.get(0).name());
    }

    @Test
    void create_ShouldSavePlant_AndInitSchedule() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        CreatePlantRequest req = new CreatePlantRequest("Basil", "Basil", null, null, 50, null, null, null, null);

        Plant savedPlant = new Plant();
        savedPlant.setId(UUID.randomUUID());
        savedPlant.setUser(user);
        savedPlant.setName("Basil");

        when(plantRepository.save(any(Plant.class))).thenReturn(savedPlant);

        // Act
        PlantResponse res = plantService.create(user, req);

        // Assert
        assertNotNull(res);
        verify(plantRepository).save(any(Plant.class));
        verify(careService).initSchedule(any(Plant.class));
    }

    @Test
    void update_ShouldModifyFields_WhenOwned() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID plantId = UUID.randomUUID();
        Plant existing = new Plant();
        existing.setId(plantId);
        existing.setUser(user);
        existing.setName("Old Name");

        UpdatePlantRequest req = new UpdatePlantRequest("New Name", null, null, null, null, null, null, null, null);

        when(plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)).thenReturn(Optional.of(existing));
        when(plantRepository.save(any(Plant.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        PlantResponse res = plantService.update(user, plantId, req);

        // Assert
        assertEquals("New Name", res.name());
    }

    @Test
    void softDelete_ShouldSetDeletedAt() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID plantId = UUID.randomUUID();
        Plant existing = new Plant();
        existing.setId(plantId);
        existing.setUser(user);

        when(plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)).thenReturn(Optional.of(existing));

        // Act
        plantService.softDelete(user, plantId);

        // Assert
        assertNotNull(existing.getDeletedAt());
        verify(plantRepository).save(existing);
    }

    @Test
    void getOwned_ShouldThrowNotFound_WhenMissing() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID plantId = UUID.randomUUID();
        when(plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppException.class, () -> plantService.getOwned(user, plantId));
    }
}
