package ma.farmsense.dto.accounting;

import ma.farmsense.entity.OcrStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

public record ReceiptResponse(
        UUID id,
        String originalFilename,
        String contentType,
        Long fileSizeBytes,
        String ocrVendor,
        LocalDate ocrDate,
        BigDecimal ocrAmount,
        String ocrCategory,
        String ocrLineItems,
        String ocrRawJson,
        Double ocrConfidence,
        OcrStatus ocrStatus,
        UUID transactionId,
        Instant createdAt,
        Instant updatedAt
) {}
