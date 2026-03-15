package ma.farmsense.dto.accounting;

import ma.farmsense.entity.OcrStatus;

import java.util.UUID;

public record ReceiptUploadResponse(
        UUID id,
        OcrStatus ocrStatus,
        String originalFilename
) {}
