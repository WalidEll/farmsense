package ma.farmsense.service;

import ma.farmsense.dto.device.ClaimRequest;
import ma.farmsense.dto.device.DeviceResponse;
import ma.farmsense.dto.device.SetupCodeResponse;
import ma.farmsense.entity.Device;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.DeviceRepository;
import ma.farmsense.repository.PlantRepository;
import ma.farmsense.repository.UserRepository;
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
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private PlantRepository plantRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void generateSetupCode_ShouldUpdateUserWithToken() {
        // Arrange
        ReflectionTestUtils.setField(deviceService, "claimTokenExpiryMinutes", 15);
        User user = User.builder().id(UUID.randomUUID()).build();

        // Act
        SetupCodeResponse res = deviceService.generateSetupCode(user);

        // Assert
        assertNotNull(res.getCode());
        assertEquals(user.getClaimToken(), res.getCode());
        verify(userRepository).save(user);
    }

    @Test
    void claim_ShouldAssignDeviceToUser_WhenTokenIsValid() {
        // Arrange
        ReflectionTestUtils.setField(deviceService, "offlineThresholdMinutes", 30);
        String token = "VALID-TOKEN";
        String deviceId = "FS-001";
        User user = User.builder()
                .id(UUID.randomUUID())
                .claimToken(token)
                .claimTokenExpiresAt(Instant.now().plusSeconds(600))
                .build();
        
        ClaimRequest req = ClaimRequest.builder()
                .deviceId(deviceId)
                .claimToken(token)
                .build();
        when(userRepository.findByClaimToken(token)).thenReturn(Optional.of(user));
        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());
        when(deviceRepository.save(any(Device.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        DeviceResponse res = deviceService.claim(req);

        // Assert
        assertNotNull(res);
        verify(deviceRepository).save(any(Device.class));
        assertNull(user.getClaimToken()); // Token consumed
    }

    @Test
    void list_ShouldReturnUserDevices() {
        // Arrange
        User user = User.builder().id(UUID.randomUUID()).build();
        Device device = Device.builder().id(UUID.randomUUID()).user(user).build();
        when(deviceRepository.findByUser(user)).thenReturn(List.of(device));

        // Act
        List<DeviceResponse> res = deviceService.list(user);

        // Assert
        assertEquals(1, res.size());
    }

    @Test
    void assign_ShouldLinkDeviceToPlant() {
        // Arrange
        User user = User.builder().id(UUID.randomUUID()).build();
        UUID deviceId = UUID.randomUUID();
        UUID plantId = UUID.randomUUID();
        Device device = Device.builder().id(deviceId).user(user).build();
        Plant plant = Plant.builder().id(plantId).user(user).build();

        when(deviceRepository.findByIdAndUser(deviceId, user)).thenReturn(Optional.of(device));
        when(plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)).thenReturn(Optional.of(plant));
        when(deviceRepository.save(any(Device.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        DeviceResponse res = deviceService.assign(user, deviceId, plantId);

        // Assert
        verify(deviceRepository).save(device);
        assertEquals(plant, device.getPlant());
    }
}
