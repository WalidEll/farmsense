package ma.farmsense.service;

import ma.farmsense.dto.device.DeviceResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReadingService {

    private final SensorReadingRepository readingRepository;
    private final DeviceRepository deviceRepository;
    private final PlantRepository plantRepository;

    public ReadingService(SensorReadingRepository readingRepository, DeviceRepository deviceRepository,
                          PlantRepository plantRepository) {
        this.readingRepository = readingRepository;
        this.deviceRepository = deviceRepository;
        this.plantRepository = plantRepository;
    }

    @Value("${farmsense.alert.offline-threshold-minutes:30}")
    private int offlineThresholdMinutes;

    @Transactional
    public DeviceResponse ingest(String deviceKey, SensorReadingRequest req) {
        Device device = deviceRepository.findByDeviceId(req.deviceId())
                .orElseThrow(() -> AppException.notFound("Unknown device: " + req.deviceId()));

        // Update heartbeat
        device.setLastSeenAt(Instant.now());
        deviceRepository.save(device);

        Plant plant = device.getPlant();
        if (plant == null)
            return DeviceResponse.from(device, offlineThresholdMinutes); // device not yet assigned — drop reading

        SensorReading reading = new SensorReading();
        reading.setDeviceId(req.deviceId());
        reading.setPlant(plant);
        reading.setTemperature(req.temperature());
        reading.setHumidity(req.humidity());
        reading.setSoilMoisture(req.soilMoisture());
        reading.setLightLux(req.lightLux());
        reading.setSource(SensorReading.Source.SENSOR);
        reading.setRecordedAt(req.recordedAt() != null ? req.recordedAt() : Instant.now());

        readingRepository.save(reading);

        return DeviceResponse.from(device, offlineThresholdMinutes);
    }

    public SensorReadingResponse latest(User user, UUID plantId) {
        Plant plant = getOwnedPlant(user, plantId);
        return readingRepository.findLatestByPlantId(plant.getId())
                .map(SensorReadingResponse::from)
                .orElseThrow(() -> AppException.notFound("No readings yet for this plant"));
    }

    public List<SensorReadingResponse> history(User user, UUID plantId, int hours) {
        Plant plant = getOwnedPlant(user, plantId);
        Instant since = Instant.now().minusSeconds((long) hours * 3600);
        return readingRepository.findByPlantAndRecordedAtAfter(plant, since)
                .stream().map(SensorReadingResponse::from).toList();
    }

    @Transactional
    public SensorReadingResponse addManual(User user, ManualReadingRequest req) {
        Plant plant = getOwnedPlant(user, req.plantId());

        SensorReading reading = new SensorReading();
        reading.setDeviceId("MANUAL");
        reading.setPlant(plant);
        reading.setTemperature(req.temperature());
        reading.setHumidity(req.humidity());
        reading.setSoilMoisture(req.soilMoisture());
        reading.setLightLux(req.lightLux());
        reading.setSource(SensorReading.Source.MANUAL);

        return SensorReadingResponse.from(readingRepository.save(reading));
    }

    private Plant getOwnedPlant(User user, UUID plantId) {
        return plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)
                .orElseThrow(() -> AppException.notFound("Plant not found"));
    }
}
