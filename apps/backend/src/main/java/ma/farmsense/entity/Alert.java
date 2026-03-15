package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Access(AccessType.FIELD)
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity = Severity.MEDIUM;

    private Double sensorValue;

    private String msgEn;
    private String msgFr;
    private String msgAr;

    private boolean waSent = false;
    private boolean pushSent = false;

    @Column(nullable = false)
    private Instant triggeredAt = Instant.now();

    private Instant ackAt;

    public Alert() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public Plant getPlant() { return this.plant; }
    public void setPlant(Plant plant) { this.plant = plant; }

    public AlertType getType() { return this.type; }
    public void setType(AlertType type) { this.type = type; }

    public Severity getSeverity() { return this.severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public Double getSensorValue() { return this.sensorValue; }
    public void setSensorValue(Double sensorValue) { this.sensorValue = sensorValue; }

    public String getMsgEn() { return this.msgEn; }
    public void setMsgEn(String msgEn) { this.msgEn = msgEn; }

    public String getMsgFr() { return this.msgFr; }
    public void setMsgFr(String msgFr) { this.msgFr = msgFr; }

    public String getMsgAr() { return this.msgAr; }
    public void setMsgAr(String msgAr) { this.msgAr = msgAr; }

    public boolean isWaSent() { return this.waSent; }
    public void setWaSent(boolean waSent) { this.waSent = waSent; }

    public boolean isPushSent() { return this.pushSent; }
    public void setPushSent(boolean pushSent) { this.pushSent = pushSent; }

    public Instant getTriggeredAt() { return this.triggeredAt; }
    public void setTriggeredAt(Instant triggeredAt) { this.triggeredAt = triggeredAt; }

    public Instant getAckAt() { return this.ackAt; }
    public void setAckAt(Instant ackAt) { this.ackAt = ackAt; }

    public enum AlertType {
        SOIL_DRY, SOIL_WET, TEMP_HIGH, TEMP_LOW, LIGHT_LOW, DEVICE_OFFLINE
    }

    public enum Severity { LOW, MEDIUM, HIGH }
}
