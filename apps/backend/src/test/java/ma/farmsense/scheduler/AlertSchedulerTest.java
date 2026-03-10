package ma.farmsense.scheduler;

import ma.farmsense.entity.Alert;
import ma.farmsense.entity.Device;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.SensorReading;
import ma.farmsense.entity.User;
import ma.farmsense.repository.AlertPreferenceRepository;
import ma.farmsense.repository.AlertRepository;
import ma.farmsense.repository.DeviceRepository;
import ma.farmsense.repository.PlantRepository;
import ma.farmsense.repository.SensorReadingRepository;
import ma.farmsense.service.WhatsAppService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertSchedulerTest {

    @Mock
    private PlantRepository plantRepo;
    @Mock
    private DeviceRepository deviceRepo;
    @Mock
    private SensorReadingRepository readingRepo;
    @Mock
    private AlertRepository alertRepo;
    @Mock
    private AlertPreferenceRepository alertPrefRepo;
    @Mock
    private WhatsAppService whatsApp;

    @InjectMocks
    private AlertScheduler alertScheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(alertScheduler, "suppressHours", 4);
        ReflectionTestUtils.setField(alertScheduler, "daylightStart", 7);
        ReflectionTestUtils.setField(alertScheduler, "daylightEnd", 19);
    }

    @Test
    void checkThresholds_ShouldFireAlert_WhenConsecutiveBreaches() {
        User user = User.builder().id(UUID.randomUUID()).phoneWa("123").lang(User.Language.EN).build();
        Plant plant = Plant.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name("Lily")
                .soilMin(30)
                .build();
        Device device = Device.builder().deviceId("DEV-1").build();

        when(plantRepo.findAllActive()).thenReturn(List.of(plant));
        when(deviceRepo.findByPlantId(plant.getId())).thenReturn(List.of(device));

        SensorReading r1 = SensorReading.builder().soilMoisture(25).recordedAt(Instant.now()).build();
        SensorReading r2 = SensorReading.builder().soilMoisture(20).recordedAt(Instant.now().minusSeconds(600)).build();

        when(readingRepo.findLatest2ByPlantAndDeviceId(plant.getId(), "DEV-1")).thenReturn(List.of(r1, r2));
        when(alertPrefRepo.findByUser(user)).thenReturn(Optional.empty());
        when(alertRepo.findMostRecentByPlantAndType(any(), any(), any())).thenReturn(Optional.empty());

        alertScheduler.checkThresholds();

        verify(alertRepo, times(2)).save(any(Alert.class));
        verify(whatsApp, times(1)).send(eq("123"), contains("Soil is dry"));
    }

    @Test
    void checkThresholds_ShouldNotFire_WhenOnlyOneBreach() {
        Plant plant = Plant.builder().id(UUID.randomUUID()).soilMin(30).build();
        Device device = Device.builder().deviceId("DEV-1").build();

        when(plantRepo.findAllActive()).thenReturn(List.of(plant));
        when(deviceRepo.findByPlantId(plant.getId())).thenReturn(List.of(device));

        SensorReading r1 = SensorReading.builder().soilMoisture(25).build();
        SensorReading r2 = SensorReading.builder().soilMoisture(35).build();

        when(readingRepo.findLatest2ByPlantAndDeviceId(plant.getId(), "DEV-1")).thenReturn(List.of(r1, r2));

        alertScheduler.checkThresholds();

        verify(alertRepo, never()).save(any());
    }

    @Test
    void checkThresholds_ShouldSuppressAlert_WhenFiredRecently() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Plant plant = Plant.builder().id(UUID.randomUUID()).user(user).soilMin(30).build();
        Device device = Device.builder().deviceId("DEV-1").build();

        when(plantRepo.findAllActive()).thenReturn(List.of(plant));
        when(deviceRepo.findByPlantId(plant.getId())).thenReturn(List.of(device));

        SensorReading r1 = SensorReading.builder().soilMoisture(20).build();
        SensorReading r2 = SensorReading.builder().soilMoisture(20).build();

        when(readingRepo.findLatest2ByPlantAndDeviceId(plant.getId(), "DEV-1")).thenReturn(List.of(r1, r2));
        when(alertPrefRepo.findByUser(user)).thenReturn(Optional.empty());

        when(alertRepo.findMostRecentByPlantAndType(any(), any(), any()))
                .thenReturn(Optional.of(new Alert()));

        alertScheduler.checkThresholds();

        verify(alertRepo, never()).save(any());
    }
}
