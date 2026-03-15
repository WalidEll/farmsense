package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.poultry.CreateCustomerRequest;
import ma.farmsense.dto.poultry.CustomerResponse;
import ma.farmsense.dto.poultry.UpdateCustomerRequest;
import ma.farmsense.entity.User;
import ma.farmsense.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAll(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(customerService.findAll(user, search));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateCustomerRequest req) {
        return ResponseEntity.status(201).body(customerService.create(user, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(customerService.findById(user, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest req) {
        return ResponseEntity.ok(customerService.update(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        customerService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
