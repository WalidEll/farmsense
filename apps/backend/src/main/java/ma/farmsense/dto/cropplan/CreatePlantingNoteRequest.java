package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotBlank;

public record CreatePlantingNoteRequest(
        @NotBlank String content
) {}
