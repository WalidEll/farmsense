package ma.farmsense.dto.accounting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.OcrStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponse {
    private UUID id;
    private String originalFilename;
    private String contentType;
    private Long fileSizeBytes;
    private String ocrVendor;
    private LocalDate ocrDate;
    private BigDecimal ocrAmount;
    private String ocrCategory;
    private String ocrLineItems;
    private String ocrRawJson;
    private Double ocrConfidence;
    private OcrStatus ocrStatus;
    private UUID transactionId;
    private Instant createdAt;
    private Instant updatedAt;
}
