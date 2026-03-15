package ma.farmsense.controller;

import ma.farmsense.dto.diagnose.*;
import ma.farmsense.entity.User;
import ma.farmsense.service.DiagnoseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/diagnose")
public class DiagnoseController {

    private final DiagnoseService diagnoseService;

    public DiagnoseController(DiagnoseService diagnoseService) {
        this.diagnoseService = diagnoseService;
    }

    /** US-050: Diagnose plant from photo */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiagnoseResponse> diagnose(
            @AuthenticationPrincipal User user,
            @RequestParam UUID plantId,
            @RequestPart("photo") MultipartFile photo) {
        return ResponseEntity.ok(diagnoseService.diagnose(user, plantId, photo));
    }

    /** US-051: Diagnosis history for a plant */
    @GetMapping("/{plantId}/history")
    public ResponseEntity<List<DiagnoseResponse>> history(
            @AuthenticationPrincipal User user,
            @PathVariable UUID plantId) {
        return ResponseEntity.ok(diagnoseService.history(user, plantId));
    }

    /** US-052: Public share link for diagnosis */
    @GetMapping("/share/{id}")
    public ResponseEntity<DiagnoseResponse> share(@PathVariable UUID id) {
        return ResponseEntity.ok(diagnoseService.getShared(id));
    }
}
