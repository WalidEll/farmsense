package ma.farmsense.dto.poultry;

import ma.farmsense.entity.MortalityCause;
import ma.farmsense.entity.MortalityRecord;
import ma.farmsense.entity.MortalityType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MortalityEventResponse(
        UUID id,
        UUID flockId,
        MortalityType type,
        Integer count,
        LocalDate mortalityDate,
        MortalityCause cause,
        String notes,
        Instant createdAt
) {
    public static MortalityEventResponse from(MortalityRecord r) {
        return new MortalityEventResponse(
                r.getId(),
                r.getFlock().getId(),
                r.getType(),
                r.getCount(),
                r.getMortalityDate(),
                r.getCause(),
                r.getNotes(),
                r.getCreatedAt()
        );
    }
}
