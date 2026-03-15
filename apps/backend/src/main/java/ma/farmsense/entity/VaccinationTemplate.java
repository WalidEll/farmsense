package ma.farmsense.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "vaccination_templates")
@Access(AccessType.FIELD)
public class VaccinationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id", nullable = false)
    private Breed breed;

    @Column(name = "vaccine_name", nullable = false)
    private String vaccineName;

    @Column(name = "recommended_age_days", nullable = false)
    private Integer recommendedAgeDays;

    @Column(length = 100)
    private String dosage;

    @Enumerated(EnumType.STRING)
    @Column(name = "administration_route", nullable = false)
    private AdministrationRoute administrationRoute;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public VaccinationTemplate() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Breed getBreed() { return this.breed; }
    public void setBreed(Breed breed) { this.breed = breed; }

    public String getVaccineName() { return this.vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public Integer getRecommendedAgeDays() { return this.recommendedAgeDays; }
    public void setRecommendedAgeDays(Integer recommendedAgeDays) { this.recommendedAgeDays = recommendedAgeDays; }

    public String getDosage() { return this.dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public AdministrationRoute getAdministrationRoute() { return this.administrationRoute; }
    public void setAdministrationRoute(AdministrationRoute administrationRoute) { this.administrationRoute = administrationRoute; }

    public Boolean getIsMandatory() { return this.isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getSortOrder() { return this.sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
