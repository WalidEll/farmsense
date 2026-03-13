package ma.farmsense.dto.poultry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.Flock;
import ma.farmsense.entity.FlockPurpose;
import ma.farmsense.entity.FlockStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FlockResponse {

    private UUID id;
    private String name;
    private String nameAr;
    private String nameEn;
    private String breed;
    private Integer birdCount;
    private Integer currentBirdCount;
    private FlockPurpose purpose;
    private FlockStatus status;
    private LocalDate startDate;
    private UUID supplierId;
    private String supplierName;
    private String source;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    public static FlockResponse from(Flock f) {
        return FlockResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .nameAr(f.getNameAr())
                .nameEn(f.getNameEn())
                .breed(f.getBreed())
                .birdCount(f.getBirdCount())
                .currentBirdCount(f.getCurrentBirdCount())
                .purpose(f.getPurpose())
                .status(f.getStatus())
                .startDate(f.getStartDate())
                .supplierId(f.getSupplier() != null ? f.getSupplier().getId() : null)
                .supplierName(f.getSupplier() != null ? f.getSupplier().getName() : null)
                .source(f.getSource())
                .notes(f.getNotes())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .build();
    }
}
