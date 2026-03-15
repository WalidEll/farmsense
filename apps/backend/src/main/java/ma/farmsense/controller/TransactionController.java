package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.accounting.*;
import ma.farmsense.dto.team.ApprovalLogResponse;
import ma.farmsense.dto.team.ApprovalRequest;
import ma.farmsense.entity.User;
import ma.farmsense.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponse> getAll(
            @AuthenticationPrincipal User user,
            @ModelAttribute TransactionFilterRequest filters) {
        return transactionService.findAll(user, filters);
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return transactionService.getDashboard(user, from, to);
    }

    @GetMapping("/categories")
    public List<String> getCategories(@AuthenticationPrincipal User user) {
        return transactionService.getCategories(user);
    }

    @GetMapping("/{id}")
    public TransactionResponse getById(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        return transactionService.findById(user, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.create(user, request);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        return transactionService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        transactionService.delete(user, id);
    }

    @PostMapping("/{id}/approve")
    public TransactionResponse approve(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody ApprovalRequest request) {
        return transactionService.approve(user, id, request);
    }

    @GetMapping("/{id}/approval-history")
    public List<ApprovalLogResponse> getApprovalLogs(@PathVariable UUID id) {
        return transactionService.getApprovalLogs(id);
    }
}
