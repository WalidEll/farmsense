package ma.farmsense.service;

import ma.farmsense.dto.alert.AlertPreferenceRequest;
import ma.farmsense.dto.alert.AlertPreferenceResponse;
import ma.farmsense.dto.alert.AlertResponse;
import ma.farmsense.entity.Alert;
import ma.farmsense.entity.AlertPreference;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.AlertPreferenceRepository;
import ma.farmsense.repository.AlertRepository;
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
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;
    @Mock
    private AlertPreferenceRepository alertPreferenceRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void findAll_ShouldReturnAlerts_ForUser() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Plant plant = Plant.builder().id(UUID.randomUUID()).name("Rose").build();
        Alert alert = Alert.builder()
                .id(UUID.randomUUID())
                .user(user)
                .plant(plant)
                .type(Alert.AlertType.SOIL_DRY)
                .severity(Alert.Severity.HIGH)
                .build();
        when(alertRepository.findByUserOrderByTriggeredAtDesc(user)).thenReturn(List.of(alert));

        List<AlertResponse> result = alertService.findAll(user);

        assertEquals(1, result.size());
        assertEquals("SOIL_DRY", result.get(0).type());
    }

    @Test
    void acknowledge_ShouldSetAckAt_WhenOwned() {
        User user = User.builder().id(UUID.randomUUID()).build();
        UUID alertId = UUID.randomUUID();
        Alert alert = Alert.builder()
                .id(alertId)
                .user(user)
                .type(Alert.AlertType.TEMP_HIGH)
                .severity(Alert.Severity.MEDIUM)
                .build();
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(i -> i.getArguments()[0]);

        AlertResponse result = alertService.acknowledge(user, alertId);

        assertNotNull(result.ackAt());
    }

    @Test
    void acknowledge_ShouldThrowNotFound_WhenNotOwned() {
        User owner = User.builder().id(UUID.randomUUID()).build();
        User other = User.builder().id(UUID.randomUUID()).build();
        UUID alertId = UUID.randomUUID();
        Alert alert = Alert.builder()
                .id(alertId)
                .user(owner)
                .type(Alert.AlertType.SOIL_DRY)
                .severity(Alert.Severity.LOW)
                .build();
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));

        assertThrows(AppException.class, () -> alertService.acknowledge(other, alertId));
    }

    @Test
    void getUnreadCount_ShouldDelegateToRepository() {
        User user = User.builder().id(UUID.randomUUID()).build();
        when(alertRepository.countByUserAndAckAtIsNull(user)).thenReturn(5L);

        assertEquals(5L, alertService.getUnreadCount(user));
    }

    @Test
    void getPreferences_ShouldCreateDefaults_WhenNoneExist() {
        User user = User.builder().id(UUID.randomUUID()).build();
        AlertPreference defaultPref = AlertPreference.builder().user(user).build();
        when(alertPreferenceRepository.findByUser(user)).thenReturn(Optional.empty());
        when(alertPreferenceRepository.save(any(AlertPreference.class))).thenReturn(defaultPref);

        AlertPreferenceResponse result = alertService.getPreferences(user);

        assertTrue(result.soilDryEnabled());
        verify(alertPreferenceRepository).save(any(AlertPreference.class));
    }

    @Test
    void updatePreferences_ShouldPersistChanges() {
        User user = User.builder().id(UUID.randomUUID()).build();
        AlertPreference existing = AlertPreference.builder().user(user).build();
        when(alertPreferenceRepository.findByUser(user)).thenReturn(Optional.of(existing));
        when(alertPreferenceRepository.save(any(AlertPreference.class))).thenAnswer(i -> i.getArguments()[0]);

        AlertPreferenceRequest req = new AlertPreferenceRequest(false, true, true, true, true, true, null, null, false, true);

        AlertPreferenceResponse result = alertService.updatePreferences(user, req);

        assertFalse(result.soilDryEnabled());
        assertFalse(result.channelWhatsapp());
    }
}
