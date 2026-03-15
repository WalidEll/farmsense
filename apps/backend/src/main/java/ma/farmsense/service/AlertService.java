package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.alert.AlertPreferenceRequest;
import ma.farmsense.dto.alert.AlertPreferenceResponse;
import ma.farmsense.dto.alert.AlertResponse;
import ma.farmsense.entity.Alert;
import ma.farmsense.entity.AlertPreference;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.AlertPreferenceRepository;
import ma.farmsense.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertPreferenceRepository alertPreferenceRepository;

    public List<AlertResponse> findAll(User user) {
        return alertRepository.findByUserOrderByTriggeredAtDesc(user)
                .stream().map(AlertResponse::from).toList();
    }

    @Transactional
    public AlertResponse acknowledge(User user, UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> AppException.notFound("Alert not found"));

        alert.setAckAt(Instant.now());
        return AlertResponse.from(alertRepository.save(alert));
    }

    public long getUnreadCount(User user) {
        return alertRepository.countByUserAndAckAtIsNull(user);
    }

    public AlertPreferenceResponse getPreferences(User user) {
        AlertPreference pref = alertPreferenceRepository.findByUser(user)
                .orElseGet(() -> alertPreferenceRepository.save(
                        AlertPreference.builder().user(user).build()
                ));
        return AlertPreferenceResponse.from(pref);
    }

    @Transactional
    public AlertPreferenceResponse updatePreferences(User user, AlertPreferenceRequest req) {
        AlertPreference pref = alertPreferenceRepository.findByUser(user)
                .orElseGet(() -> AlertPreference.builder().user(user).build());

        pref.setSoilDryEnabled(req.soilDryEnabled());
        pref.setSoilWetEnabled(req.soilWetEnabled());
        pref.setTempHighEnabled(req.tempHighEnabled());
        pref.setTempLowEnabled(req.tempLowEnabled());
        pref.setLightLowEnabled(req.lightLowEnabled());
        pref.setDeviceOfflineEnabled(req.deviceOfflineEnabled());
        pref.setQuietHoursStart(req.quietHoursStart());
        pref.setQuietHoursEnd(req.quietHoursEnd());
        pref.setChannelWhatsapp(req.channelWhatsapp());
        pref.setChannelPush(req.channelPush());

        return AlertPreferenceResponse.from(alertPreferenceRepository.save(pref));
    }
}
