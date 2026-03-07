package ma.farmsense.dto.device;

import lombok.Data;

import java.util.UUID;

@Data
public class AssignRequest {

    private UUID plantId;
}
