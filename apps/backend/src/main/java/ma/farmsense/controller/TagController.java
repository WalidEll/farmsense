package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.accounting.CreateTagRequest;
import ma.farmsense.dto.accounting.TagResponse;
import ma.farmsense.dto.accounting.UpdateTagRequest;
import ma.farmsense.entity.User;
import ma.farmsense.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tagService.findAll(user));
    }

    @PostMapping
    public ResponseEntity<TagResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateTagRequest req) {
        return ResponseEntity.status(201).body(tagService.create(user, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTagRequest req) {
        return ResponseEntity.ok(tagService.update(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        tagService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
