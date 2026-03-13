package ma.farmsense.service;

import ma.farmsense.dto.sensor.ManualReadingRequest;
import ma.farmsense.dto.sensor.SensorReadingRequest;
import ma.farmsense.dto.sensor.SensorReadingResponse;
import ma.farmsense.entity.Device;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.SensorReading;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.DeviceRepository;
import ma.farmsense.repository.PlantRepository;
import ma.farmsense.repository.SensorReadingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingServiceTest {

    @Mock
    private SensorReadingRepository readingRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private PlantRepository plantRepository;

    @InjectMocks
    private ReadingService readingService;

    @Test
    void ingest_ShouldSaveReading_WhenDeviceIsAssigned() {
        // Arrange
        ReflectionTestUtils.setField(readingService, "offlineThresholdMinutes", 30);
        String deviceId = "FS-001";
        Plant plant = Plant.builder().id(UUID.randomUUID()).build();
        Device device = Device.builder().deviceId(deviceId).plant(plant).build();
        
        SensorReadingRequest req = SensorReadingRequest.builder()
                .deviceId(deviceId)
                .temperature(25.0)
                .soilMoisture(60)
                .build();

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));

        // Act
        readingService.ingest("key", req);

        // Assert
        verify(readingRepository).save(any(SensorReading.class));
        verify(deviceRepository).save(device);
        assertNotNull(device.getLastSeenAt());
    }

    @Test
    void latest_ShouldReturnLatestReading() {
        // Arrange
        User user = User.builder().id(UUID.randomUUID()).build();
        UUID plantId = UUID.randomUUID();
        Plant plant = Plant.builder().id(plantId).user(user).build();
        SensorReading reading = SensorReading.builder().soilMoisture(55).recordedAt(Instant.now()).build();

        when(plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)).thenReturn(Optional.of(plant));
        when(readingRepository.findLatestByPlantId(plantId)).thenReturn(Optional.of(reading));

        // Act
        SensorReadingResponse res = readingService.latest(user, plantId);

        // Assert
        assertEquals(55, res.getSoilMoisture());
    }

    @Test
    void history_ShouldReturnList_ForTimeRange() {
        // Arrange
        User user = User.builder().id(UUID.randomUUID()).build();
        UUID plantId = UUID.randomUUID();
        Plant plant = Plant.builder().id(plantId).user(user).build();
        SensorReading reading = SensorReading.builder().recordedAt(Instant.now()).build();

        when(plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)).thenReturn(Optional.of(plant));
        when(readingRepository.findByPlantAndRecordedAtAfter(eq(plant), any(Instant.class))).thenReturn(List.of(reading));

        // Act
        List<SensorReadingResponse> res = readingService.history(user, plantId, 24);

        // Assert
        assertEquals(1, res.size());
    }

    @Test
    void addManual_ShouldSaveManualReading() {
        // Arrange
        User user = User.builder().id(UUID.randomUUID()).build();
        UUID plantId = UUID.randomUUID();
        Plant plant = Plant.builder().id(plantId).user(user).build();
        ManualReadingRequest req = ManualReadingRequest.builder().plantId(plantId).soilMoisture(45).build();

        when(plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)).thenReturn(Optional.of(plant));
        when(readingRepository.save(any(SensorReading.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        SensorReadingResponse res = readingService.addManual(user, req);

        // Assert
        assertEquals(45, res.getSoilMoisture());
        verify(readingRepository).save(any(SensorReading.class));
    }
}
