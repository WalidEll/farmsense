package ma.farmsense.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "alert_preferences")
@Access(AccessType.FIELD)
public class AlertPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private boolean soilDryEnabled = true;
    private boolean soilWetEnabled = true;
    private boolean tempHighEnabled = true;
    private boolean tempLowEnabled = true;
    private boolean lightLowEnabled = true;
    private boolean deviceOfflineEnabled = true;

    /** Quiet hours start (0-23), nullable means no quiet hours */
    private Integer quietHoursStart;

    /** Quiet hours end (0-23), nullable means no quiet hours */
    private Integer quietHoursEnd;

    private boolean channelWhatsapp = true;
    private boolean channelPush = true;

    public AlertPreference() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public boolean isSoilDryEnabled() { return this.soilDryEnabled; }
    public void setSoilDryEnabled(boolean soilDryEnabled) { this.soilDryEnabled = soilDryEnabled; }

    public boolean isSoilWetEnabled() { return this.soilWetEnabled; }
    public void setSoilWetEnabled(boolean soilWetEnabled) { this.soilWetEnabled = soilWetEnabled; }

    public boolean isTempHighEnabled() { return this.tempHighEnabled; }
    public void setTempHighEnabled(boolean tempHighEnabled) { this.tempHighEnabled = tempHighEnabled; }

    public boolean isTempLowEnabled() { return this.tempLowEnabled; }
    public void setTempLowEnabled(boolean tempLowEnabled) { this.tempLowEnabled = tempLowEnabled; }

    public boolean isLightLowEnabled() { return this.lightLowEnabled; }
    public void setLightLowEnabled(boolean lightLowEnabled) { this.lightLowEnabled = lightLowEnabled; }

    public boolean isDeviceOfflineEnabled() { return this.deviceOfflineEnabled; }
    public void setDeviceOfflineEnabled(boolean deviceOfflineEnabled) { this.deviceOfflineEnabled = deviceOfflineEnabled; }

    public Integer getQuietHoursStart() { return this.quietHoursStart; }
    public void setQuietHoursStart(Integer quietHoursStart) { this.quietHoursStart = quietHoursStart; }

    public Integer getQuietHoursEnd() { return this.quietHoursEnd; }
    public void setQuietHoursEnd(Integer quietHoursEnd) { this.quietHoursEnd = quietHoursEnd; }

    public boolean isChannelWhatsapp() { return this.channelWhatsapp; }
    public void setChannelWhatsapp(boolean channelWhatsapp) { this.channelWhatsapp = channelWhatsapp; }

    public boolean isChannelPush() { return this.channelPush; }
    public void setChannelPush(boolean channelPush) { this.channelPush = channelPush; }
}
