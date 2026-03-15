package ma.farmsense.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "crop_issues")
@Access(AccessType.FIELD)
public class CropIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType issueType;

    @Column(nullable = false)
    private String name;

    private String nameAr;
    private String nameEn;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String symptomsAr;

    @Column(columnDefinition = "TEXT")
    private String symptomsEn;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Column(columnDefinition = "TEXT")
    private String treatmentAr;

    @Column(columnDefinition = "TEXT")
    private String treatmentEn;

    @Column(columnDefinition = "TEXT")
    private String prevention;

    public CropIssue() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Crop getCrop() { return this.crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public IssueType getIssueType() { return this.issueType; }
    public void setIssueType(IssueType issueType) { this.issueType = issueType; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getNameAr() { return this.nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getNameEn() { return this.nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getSymptoms() { return this.symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getSymptomsAr() { return this.symptomsAr; }
    public void setSymptomsAr(String symptomsAr) { this.symptomsAr = symptomsAr; }

    public String getSymptomsEn() { return this.symptomsEn; }
    public void setSymptomsEn(String symptomsEn) { this.symptomsEn = symptomsEn; }

    public String getTreatment() { return this.treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getTreatmentAr() { return this.treatmentAr; }
    public void setTreatmentAr(String treatmentAr) { this.treatmentAr = treatmentAr; }

    public String getTreatmentEn() { return this.treatmentEn; }
    public void setTreatmentEn(String treatmentEn) { this.treatmentEn = treatmentEn; }

    public String getPrevention() { return this.prevention; }
    public void setPrevention(String prevention) { this.prevention = prevention; }
}
