package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.accounting.CreateLaborLogRequest;
import ma.farmsense.dto.accounting.LaborLogResponse;
import ma.farmsense.dto.accounting.UpdateLaborLogRequest;
import ma.farmsense.entity.User;
import ma.farmsense.service.LaborService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/labor")
@RequiredArgsConstructor
public class LaborController {

    private final LaborService laborService;

    @GetMapping
    public List<LaborLogResponse> getAll(@AuthenticationPrincipal User user) {
        return laborService.findAll(user);
    }

    @GetMapping("/{id}")
    public LaborLogResponse getById(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        return laborService.findById(user, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LaborLogResponse create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateLaborLogRequest request) {
        return laborService.create(user, request);
    }

    @PutMapping("/{id}")
    public LaborLogResponse update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLaborLogRequest request) {
        return laborService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        laborService.delete(user, id);
    }
}
