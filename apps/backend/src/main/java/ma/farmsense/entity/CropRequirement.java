package ma.farmsense.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "crop_requirements")
@Access(AccessType.FIELD)
public class CropRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    private Integer soilMoistureMin;
    private Integer soilMoistureMax;
    private Double tempMin;
    private Double tempMax;
    private Integer lightMin;
    private Integer lightMax;
    private Double humidityMin;
    private Double humidityMax;
    private String soilType;
    private Double phMin;
    private Double phMax;
    private String waterFrequency;

    public CropRequirement() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Crop getCrop() { return this.crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public Integer getSoilMoistureMin() { return this.soilMoistureMin; }
    public void setSoilMoistureMin(Integer soilMoistureMin) { this.soilMoistureMin = soilMoistureMin; }

    public Integer getSoilMoistureMax() { return this.soilMoistureMax; }
    public void setSoilMoistureMax(Integer soilMoistureMax) { this.soilMoistureMax = soilMoistureMax; }

    public Double getTempMin() { return this.tempMin; }
    public void setTempMin(Double tempMin) { this.tempMin = tempMin; }

    public Double getTempMax() { return this.tempMax; }
    public void setTempMax(Double tempMax) { this.tempMax = tempMax; }

    public Integer getLightMin() { return this.lightMin; }
    public void setLightMin(Integer lightMin) { this.lightMin = lightMin; }

    public Integer getLightMax() { return this.lightMax; }
    public void setLightMax(Integer lightMax) { this.lightMax = lightMax; }

    public Double getHumidityMin() { return this.humidityMin; }
    public void setHumidityMin(Double humidityMin) { this.humidityMin = humidityMin; }

    public Double getHumidityMax() { return this.humidityMax; }
    public void setHumidityMax(Double humidityMax) { this.humidityMax = humidityMax; }

    public String getSoilType() { return this.soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public Double getPhMin() { return this.phMin; }
    public void setPhMin(Double phMin) { this.phMin = phMin; }

    public Double getPhMax() { return this.phMax; }
    public void setPhMax(Double phMax) { this.phMax = phMax; }

    public String getWaterFrequency() { return this.waterFrequency; }
    public void setWaterFrequency(String waterFrequency) { this.waterFrequency = waterFrequency; }
}
