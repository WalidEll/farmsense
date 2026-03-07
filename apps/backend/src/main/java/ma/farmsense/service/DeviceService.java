package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.device.ClaimRequest;
import ma.farmsense.dto.device.DeviceConfigRequest;
import ma.farmsense.dto.device.DeviceResponse;
import ma.farmsense.dto.device.SetupCodeResponse;
import ma.farmsense.entity.Device;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.DeviceRepository;
import ma.farmsense.repository.PlantRepository;
import ma.farmsense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;

    @Value("${farmsense.device.claim-token-expiry-minutes:15}")
    private int claimTokenExpiryMinutes;

    @Value("${farmsense.alert.offline-threshold-minutes:30}")
    private int offlineThresholdMinutes;

    /** US-030: Generate a one-time setup code stored on the User entity */
    @Transactional
    public SetupCodeResponse generateSetupCode(User user) {
        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant expiry = Instant.now().plusSeconds((long) claimTokenExpiryMinutes * 60);

        user.setClaimToken(code);
        user.setClaimTokenExpiresAt(expiry);
        userRepository.save(user);

        return SetupCodeResponse.builder().code(code).expiresAt(expiry).build();
    }

    /** US-032: ESP32 claims itself using device_id + claim_token */
    @Transactional
    public DeviceResponse claim(ClaimRequest req) {
        User owner = userRepository.findByClaimToken(req.getClaimToken())
                .orElseThrow(() -> AppException.badRequest("Invalid or expired claim token"));

        if (owner.getClaimTokenExpiresAt() == null
                || owner.getClaimTokenExpiresAt().isBefore(Instant.now())) {
            throw AppException.badRequest("Claim token has expired");
        }

        Device device = deviceRepository.findByDeviceId(req.getDeviceId())
                .orElseGet(() -> Device.builder().deviceId(req.getDeviceId()).build());

        device.setUser(owner);
        device.setClaimedAt(Instant.now());
        deviceRepository.save(device);

        // Consume the token
        owner.setClaimToken(null);
        owner.setClaimTokenExpiresAt(null);
        userRepository.save(owner);

        return DeviceResponse.from(device, offlineThresholdMinutes);
    }

    public List<DeviceResponse> list(User user) {
        return deviceRepository.findByUser(user)
                .stream().map(d -> DeviceResponse.from(d, offlineThresholdMinutes)).toList();
    }

    @Transactional
    public DeviceResponse assign(User user, UUID deviceId, UUID plantId) {
        Device device = getOwned(user, deviceId);

        Plant plant = plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)
                .orElseThrow(() -> AppException.notFound("Plant not found"));

        device.setPlant(plant);
        return DeviceResponse.from(deviceRepository.save(device), offlineThresholdMinutes);
    }

    @Transactional
    public DeviceResponse label(User user, UUID deviceId, String label) {
        Device device = getOwned(user, deviceId);
        device.setLabel(label);
        return DeviceResponse.from(deviceRepository.save(device), offlineThresholdMinutes);
    }

    @Transactional
    public DeviceResponse updateConfig(User user, UUID deviceId, DeviceConfigRequest req) {
        Device device = getOwned(user, deviceId);
        device.setReadIntervalMs(req.getReadIntervalMs());
        device.setSoilDryValue(req.getSoilDryValue());
        device.setSoilWetValue(req.getSoilWetValue());
        return DeviceResponse.from(deviceRepository.save(device), offlineThresholdMinutes);
    }

    @Transactional
    public void delete(User user, UUID deviceId) {
        Device device = getOwned(user, deviceId);
        deviceRepository.delete(device);
    }

    private Device getOwned(User user, UUID id) {
        return deviceRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> AppException.notFound("Device not found"));
    }
}
