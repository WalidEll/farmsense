package ma.farmsense.service;

import ma.farmsense.entity.Plant;
import ma.farmsense.entity.SensorReading;
import ma.farmsense.entity.User;
import ma.farmsense.repository.CareLogRepository;
import ma.farmsense.repository.PlantRepository;
import ma.farmsense.repository.SensorReadingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareServiceTest {

    @Mock
    private PlantRepository plantRepo;
    @Mock
    private SensorReadingRepository readingRepo;
    @Mock
    private CareLogRepository careLogRepo;

    @InjectMocks
    private CareService careService;

    @Test
    void getSchedule_ShouldAdjustWatering_WhenSoilIsDry() {
        // Arrange
        UUID plantId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Plant plant = new Plant();
        plant.setId(plantId);
        plant.setUser(user);
        plant.setSoilMin(40);
        plant.setWateringIntervalDays(7);
        plant.setLastWateredAt(Instant.now().minus(5, ChronoUnit.DAYS));

        when(plantRepo.findByIdAndUserAndDeletedAtIsNull(eq(plantId), eq(user))).thenReturn(Optional.of(plant));

        // Latest reading shows 30% (below 40% min)
        SensorReading latest = new SensorReading();
        latest.setSoilMoisture(30);
        latest.setRecordedAt(Instant.now());
        when(readingRepo.findLatestByPlantId(plantId)).thenReturn(Optional.of(latest));

        // Act
        var schedule = careService.getSchedule(user, plantId);

        // Assert
        assertTrue(schedule.watering().overdue(), "Watering should be overdue because soil is dry");
        assertEquals(0, schedule.watering().daysRemaining());
    }

    @Test
    void getSchedule_ShouldNotAdjustWatering_WhenSoilIsWet() {
        UUID plantId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Plant plant = new Plant();
        plant.setId(plantId);
        plant.setUser(user);
        plant.setSoilMin(40);
        plant.setWateringIntervalDays(7);
        plant.setLastWateredAt(Instant.now().minus(2, ChronoUnit.DAYS));

        when(plantRepo.findByIdAndUserAndDeletedAtIsNull(eq(plantId), eq(user))).thenReturn(Optional.of(plant));

        // Latest reading shows 60% (above 40% min)
        SensorReading latest = new SensorReading();
        latest.setSoilMoisture(60);
        latest.setRecordedAt(Instant.now());
        when(readingRepo.findLatestByPlantId(plantId)).thenReturn(Optional.of(latest));

        var schedule = careService.getSchedule(user, plantId);

        assertFalse(schedule.watering().overdue(), "Watering should not be overdue when soil is wet");
    }
}
