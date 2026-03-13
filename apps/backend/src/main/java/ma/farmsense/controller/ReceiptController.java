package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.accounting.ReceiptConfirmRequest;
import ma.farmsense.dto.accounting.ReceiptResponse;
import ma.farmsense.dto.accounting.ReceiptUploadResponse;
import ma.farmsense.dto.accounting.TransactionResponse;
import ma.farmsense.entity.User;
import ma.farmsense.service.ReceiptService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping
    public List<ReceiptResponse> getAll(@AuthenticationPrincipal User user) {
        return receiptService.findAll(user);
    }

    @GetMapping("/{id}")
    public ReceiptResponse getById(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        return receiptService.findById(user, id);
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ReceiptUploadResponse upload(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        return receiptService.upload(user, file);
    }

    @PostMapping("/{id}/confirm")
    public TransactionResponse confirm(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody ReceiptConfirmRequest request) {
        return receiptService.confirm(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        receiptService.delete(user, id);
    }
}
