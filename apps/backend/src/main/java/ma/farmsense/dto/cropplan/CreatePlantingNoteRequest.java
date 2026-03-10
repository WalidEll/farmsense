package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePlantingNoteRequest {

    @NotBlank
    private String content;
}
