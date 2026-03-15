package ma.farmsense.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.farmsense.dto.accounting.*;
import ma.farmsense.entity.*;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.ReceiptRepository;
import ma.farmsense.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReceiptService {

    private static final Logger log = LoggerFactory.getLogger(ReceiptService.class);

    private final ReceiptRepository receiptRepository;
    private final TransactionService transactionService;
    private final WebClient webClient;
    private final String model;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    @Value("${farmsense.app.upload-dir:uploads/receipts}")
    private String uploadDir;

    public ReceiptService(
            ReceiptRepository receiptRepository,
            TransactionService transactionService,
            WebClient.Builder webClientBuilder,
            @Value("${farmsense.anthropic.api-url}") String apiUrl,
            @Value("${farmsense.anthropic.model}") String model,
            @Value("${farmsense.anthropic.api-key}") String apiKey,
            ObjectMapper objectMapper) {
        this.receiptRepository = receiptRepository;
        this.transactionService = transactionService;
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.model = model;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ReceiptUploadResponse upload(User user, MultipartFile file) {
        try {
            Path root = Paths.get(uploadDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = root.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            Receipt receipt = new Receipt();
            receipt.setUser(user);
            receipt.setOriginalFilename(file.getOriginalFilename());
            receipt.setFilePath(filePath.toString());
            receipt.setContentType(file.getContentType());
            receipt.setFileSizeBytes(file.getSize());
            receipt.setOcrStatus(OcrStatus.PENDING);

            Receipt saved = receiptRepository.save(receipt);
            
            // Trigger OCR processing asynchronously
            processOcrAsync(saved.getId());

            return new ReceiptUploadResponse(
                    saved.getId(),
                    saved.getOcrStatus(),
                    saved.getOriginalFilename()
            );
        } catch (IOException e) {
            log.error("Failed to store receipt: {}", e.getMessage());
            throw AppException.badRequest("Could not store receipt file");
        }
    }

    @Async
    @Transactional
    public void processOcrAsync(UUID receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId).orElse(null);
        if (receipt == null) return;

        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(receipt.getFilePath()));
            String base64Image = Base64.getEncoder().encodeToString(fileContent);
            String mediaType = receipt.getContentType() != null ? receipt.getContentType() : "image/jpeg";

            String prompt = "You are a receipt OCR expert. Extract the following information from this receipt image in JSON format:\n" +
                    "- date (YYYY-MM-DD)\n" +
                    "- total_amount (number)\n" +
                    "- category (e.g., Feed, Seeds, Fuel, Equipment, Labor, Other)\n" +
                    "- vendor (string)\n" +
                    "- items (list of {description, amount, quantity})\n" +
                    "Return ONLY the raw JSON object.";

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "max_tokens", 1024,
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of(
                                            "type", "image",
                                            "source", Map.of(
                                                    "type", "base64",
                                                    "media_type", mediaType,
                                                    "data", base64Image)),
                                    Map.of("type", "text", "text", prompt)))));

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("content")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
                if (!content.isEmpty()) {
                    String jsonText = (String) content.get(0).get("text");
                    // Clean up JSON if Claude adds markdown blocks
                    jsonText = jsonText.replaceAll("```json", "").replaceAll("```", "").trim();
                    
                    Map<String, Object> ocrData = objectMapper.readValue(jsonText, Map.class);
                    
                    receipt.setOcrRawJson(jsonText);
                    if (ocrData.get("date") != null) receipt.setOcrDate(LocalDate.parse((String) ocrData.get("date")));
                    if (ocrData.get("total_amount") != null) {
                        Object amount = ocrData.get("total_amount");
                        receipt.setOcrAmount(new BigDecimal(amount.toString()));
                    }
                    if (ocrData.get("category") != null) receipt.setOcrCategory((String) ocrData.get("category"));
                    if (ocrData.get("vendor") != null) receipt.setOcrVendor((String) ocrData.get("vendor"));
                    if (ocrData.get("items") != null) receipt.setOcrLineItems(objectMapper.writeValueAsString(ocrData.get("items")));
                    
                    receipt.setOcrStatus(OcrStatus.PROCESSED);
                }
            } else {
                receipt.setOcrStatus(OcrStatus.FAILED);
            }
        } catch (Exception e) {
            log.error("OCR processing failed for receipt {}: {}", receiptId, e.getMessage());
            receipt.setOcrStatus(OcrStatus.FAILED);
        }
        receiptRepository.save(receipt);
    }

    public List<ReceiptResponse> findAll(User user) {
        return receiptRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToResponse).toList();
    }

    public ReceiptResponse findById(User user, UUID id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Receipt not found"));
        if (!receipt.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Receipt not found");
        }
        return mapToResponse(receipt);
    }

    @Transactional
    public TransactionResponse confirm(User user, UUID id, ReceiptConfirmRequest req) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Receipt not found"));
        if (!receipt.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Receipt not found");
        }

        CreateTransactionRequest createReq = new CreateTransactionRequest(
                req.type(),
                req.category(),
                req.subcategory(),
                req.amount(),
                null, // quantity
                null, // unitPrice
                req.transactionDate(),
                req.description(),
                req.paymentMethod(),
                null, // referenceNumber
                req.supplierId(),
                req.customerId(),
                receipt.getId(),
                req.flockId(),
                req.cropPlanId(),
                req.farmLocationId(),
                req.tagIds(),
                null  // notes
        );

        TransactionResponse response = transactionService.create(user, createReq);
        
        // Link transaction back to receipt is done by OneToOne mappedBy
        // But we might want to update some status if needed
        
        return response;
    }

    @Transactional
    public void delete(User user, UUID id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Receipt not found"));
        if (!receipt.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Receipt not found");
        }
        
        try {
            Files.deleteIfExists(Paths.get(receipt.getFilePath()));
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", receipt.getFilePath());
        }
        
        receiptRepository.delete(receipt);
    }

    private ReceiptResponse mapToResponse(Receipt r) {
        return new ReceiptResponse(
                r.getId(),
                r.getOriginalFilename(),
                r.getContentType(),
                r.getFileSizeBytes(),
                r.getOcrVendor(),
                r.getOcrDate(),
                r.getOcrAmount(),
                r.getOcrCategory(),
                r.getOcrLineItems(),
                r.getOcrRawJson(),
                r.getOcrConfidence(),
                r.getOcrStatus(),
                r.getTransaction() != null ? r.getTransaction().getId() : null,
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
