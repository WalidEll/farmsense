package ma.farmsense.service;

import lombok.extern.slf4j.Slf4j;
import ma.farmsense.dto.diagnose.DiagnoseResponse;
import ma.farmsense.entity.Diagnosis;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.DiagnosisRepository;
import ma.farmsense.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class DiagnoseService {

        private final WebClient webClient;
        private final String model;
        private final String apiKey;
        private final DiagnosisRepository diagnosisRepository;
        private final PlantRepository plantRepository;

        public DiagnoseService(
                        WebClient.Builder webClientBuilder,
                        @Value("${farmsense.anthropic.api-url}") String apiUrl,
                        @Value("${farmsense.anthropic.model}") String model,
                        @Value("${farmsense.anthropic.api-key}") String apiKey,
                        DiagnosisRepository diagnosisRepository,
                        PlantRepository plantRepository) {
                this.webClient = webClientBuilder.baseUrl(apiUrl).build();
                this.model = model;
                this.apiKey = apiKey;
                this.diagnosisRepository = diagnosisRepository;
                this.plantRepository = plantRepository;
        }

        @Value("${farmsense.app.base-url:https://app.farmsense.ma}")
        private String appBaseUrl;

        @Transactional
        public DiagnoseResponse diagnose(User user, UUID plantId, MultipartFile photo) {
                Plant plant = plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)
                                .orElseThrow(() -> AppException.notFound("Plant not found"));

                String base64Image;
                String mediaType;
                try {
                        base64Image = Base64.getEncoder().encodeToString(photo.getBytes());
                        mediaType = photo.getContentType() != null ? photo.getContentType() : "image/jpeg";
                } catch (Exception e) {
                        throw AppException.badRequest("Could not read photo");
                }

                String rawResponse = callClaude(plant.getName(), plant.getSpecies(), base64Image, mediaType);

                Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.builder()
                                .user(user)
                                .plant(plant)
                                .rawResponse(rawResponse)
                                .build());

                return toResponse(diagnosis);
        }

        public List<DiagnoseResponse> history(User user, UUID plantId) {
                Plant plant = plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)
                                .orElseThrow(() -> AppException.notFound("Plant not found"));

                return diagnosisRepository.findByUserAndPlantOrderByCreatedAtDesc(user, plant)
                                .stream().map(this::toResponse).toList();
        }

        /** US-052: Public share link — expires after 7 days */
        public DiagnoseResponse getShared(UUID id) {
                Diagnosis d = diagnosisRepository.findById(id)
                                .orElseThrow(() -> AppException.notFound("Diagnosis not found"));

                if (d.getCreatedAt().isBefore(java.time.Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS))) {
                        throw AppException.badRequest("Shared link has expired (7 days)");
                }

                return toResponse(d);
        }

        private DiagnoseResponse toResponse(Diagnosis d) {
                DiagnoseResponse res = DiagnoseResponse.from(d);
                res.setShareUrl(String.format("%s/diagnose/share/%s", appBaseUrl, d.getId()));
                return res;
        }

        private String callClaude(String plantName, String species, String base64Image, String mediaType) {
                String prompt = String.format(
                                "You are a plant health expert. Analyze this photo of '%s' (%s) and:\n" +
                                                "1. Identify any visible diseases, pests, or deficiencies\n" +
                                                "2. Rate the plant health (Excellent/Good/Fair/Poor)\n" +
                                                "3. Recommend specific treatment steps\n" +
                                                "Keep your response concise and practical.",
                                plantName, species != null ? species : "unknown species");

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

                try {
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
                                        return (String) content.get(0).get("text");
                                }
                        }
                } catch (Exception e) {
                        log.error("Claude Vision call failed: {}", e.getMessage());
                }

                return "Diagnosis unavailable — please try again.";
        }
}
