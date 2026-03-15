package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "diagnoses")
@Access(AccessType.FIELD)
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @Column(nullable = false)
    private String photoUrl = "INLINE"; // set when photo is persisted to storage

    private String problemName;
    private String severity; // LOW / MEDIUM / HIGH

    @Column(columnDefinition = "TEXT")
    private String treatmentFr;

    @Column(columnDefinition = "TEXT")
    private String treatmentAr;

    @Column(columnDefinition = "TEXT")
    private String treatmentEn;

    @Column(columnDefinition = "TEXT")
    private String prevention;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Diagnosis() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public Plant getPlant() { return this.plant; }
    public void setPlant(Plant plant) { this.plant = plant; }

    public String getPhotoUrl() { return this.photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getProblemName() { return this.problemName; }
    public void setProblemName(String problemName) { this.problemName = problemName; }

    public String getSeverity() { return this.severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getTreatmentFr() { return this.treatmentFr; }
    public void setTreatmentFr(String treatmentFr) { this.treatmentFr = treatmentFr; }

    public String getTreatmentAr() { return this.treatmentAr; }
    public void setTreatmentAr(String treatmentAr) { this.treatmentAr = treatmentAr; }

    public String getTreatmentEn() { return this.treatmentEn; }
    public void setTreatmentEn(String treatmentEn) { this.treatmentEn = treatmentEn; }

    public String getPrevention() { return this.prevention; }
    public void setPrevention(String prevention) { this.prevention = prevention; }

    public String getRawResponse() { return this.rawResponse; }
    public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
