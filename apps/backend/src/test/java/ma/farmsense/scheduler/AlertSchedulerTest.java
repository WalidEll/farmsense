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
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPhoneWa("123");
        user.setLang(User.Language.EN);
        Plant plant = new Plant();
        plant.setId(UUID.randomUUID());
        plant.setUser(user);
        plant.setName("Lily");
        plant.setSoilMin(30);
        Device device = new Device();
        device.setDeviceId("DEV-1");

        when(plantRepo.findAllActive()).thenReturn(List.of(plant));
        when(deviceRepo.findByPlantId(plant.getId())).thenReturn(List.of(device));

        SensorReading r1 = new SensorReading();
        r1.setSoilMoisture(25);
        r1.setRecordedAt(Instant.now());
        SensorReading r2 = new SensorReading();
        r2.setSoilMoisture(20);
        r2.setRecordedAt(Instant.now().minusSeconds(600));

        when(readingRepo.findLatest2ByPlantAndDeviceId(plant.getId(), "DEV-1")).thenReturn(List.of(r1, r2));
        when(alertPrefRepo.findByUser(user)).thenReturn(Optional.empty());
        when(alertRepo.findMostRecentByPlantAndType(any(), any(), any())).thenReturn(Optional.empty());

        alertScheduler.checkThresholds();

        verify(alertRepo, times(2)).save(any(Alert.class));
        verify(whatsApp, times(1)).send(eq("123"), contains("Soil is dry"));
    }

    @Test
    void checkThresholds_ShouldNotFire_WhenOnlyOneBreach() {
        Plant plant = new Plant();
        plant.setId(UUID.randomUUID());
        plant.setSoilMin(30);
        Device device = new Device();
        device.setDeviceId("DEV-1");

        when(plantRepo.findAllActive()).thenReturn(List.of(plant));
        when(deviceRepo.findByPlantId(plant.getId())).thenReturn(List.of(device));

        SensorReading r1 = new SensorReading();
        r1.setSoilMoisture(25);
        SensorReading r2 = new SensorReading();
        r2.setSoilMoisture(35);

        when(readingRepo.findLatest2ByPlantAndDeviceId(plant.getId(), "DEV-1")).thenReturn(List.of(r1, r2));

        alertScheduler.checkThresholds();

        verify(alertRepo, never()).save(any());
    }

    @Test
    void checkThresholds_ShouldSuppressAlert_WhenFiredRecently() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Plant plant = new Plant();
        plant.setId(UUID.randomUUID());
        plant.setUser(user);
        plant.setSoilMin(30);
        Device device = new Device();
        device.setDeviceId("DEV-1");

        when(plantRepo.findAllActive()).thenReturn(List.of(plant));
        when(deviceRepo.findByPlantId(plant.getId())).thenReturn(List.of(device));

        SensorReading r1 = new SensorReading();
        r1.setSoilMoisture(20);
        SensorReading r2 = new SensorReading();
        r2.setSoilMoisture(20);

        when(readingRepo.findLatest2ByPlantAndDeviceId(plant.getId(), "DEV-1")).thenReturn(List.of(r1, r2));
        when(alertPrefRepo.findByUser(user)).thenReturn(Optional.empty());

        when(alertRepo.findMostRecentByPlantAndType(any(), any(), any()))
                .thenReturn(Optional.of(new Alert()));

        alertScheduler.checkThresholds();

        verify(alertRepo, never()).save(any());
    }
}
