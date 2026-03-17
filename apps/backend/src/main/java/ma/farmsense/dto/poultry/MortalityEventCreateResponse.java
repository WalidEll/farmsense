package ma.farmsense.dto.poultry;

import ma.farmsense.entity.MortalityCause;
import ma.farmsense.entity.MortalityRecord;
import ma.farmsense.entity.MortalityType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MortalityEventCreateResponse(
        UUID id,
        UUID flockId,
        MortalityType type,
        Integer count,
        LocalDate mortalityDate,
        MortalityCause cause,
        String notes,
        Integer updatedFlockHeadcount,
        Instant createdAt
) {
    public static MortalityEventCreateResponse from(MortalityRecord r, Integer updatedHeadcount) {
        return new MortalityEventCreateResponse(
                r.getId(),
                r.getFlock().getId(),
                r.getType(),
                r.getCount(),
                r.getMortalityDate(),
                r.getCause(),
                r.getNotes(),
                updatedHeadcount,
                r.getCreatedAt()
        );
    }
}
