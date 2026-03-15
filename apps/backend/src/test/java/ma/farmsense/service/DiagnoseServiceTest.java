package ma.farmsense.service;

import ma.farmsense.dto.diagnose.DiagnoseResponse;
import ma.farmsense.entity.Diagnosis;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.DiagnosisRepository;
import ma.farmsense.repository.PlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnoseServiceTest {

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    private DiagnoseService diagnoseService;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        diagnoseService = new DiagnoseService(
                webClientBuilder,
                "http://api",
                "claude",
                "key",
                diagnosisRepository,
                plantRepository);
        ReflectionTestUtils.setField(diagnoseService, "appBaseUrl", "https://app.farmsense.ma");
    }

    @Test
    void getShared_ShouldReturnResponse_WhenValid() {
        UUID id = UUID.randomUUID();
        Diagnosis d = Diagnosis.builder()
                .id(id)
                .createdAt(Instant.now())
                .problemName("Leaf Spot")
                .build();

        when(diagnosisRepository.findById(id)).thenReturn(Optional.of(d));

        DiagnoseResponse res = diagnoseService.getShared(id);

        assertNotNull(res);
        assertEquals("Leaf Spot", res.problemName());
        assertTrue(res.shareUrl().contains(id.toString()));
    }

    @Test
    void getShared_ShouldThrowException_WhenExpired() {
        UUID id = UUID.randomUUID();
        Diagnosis d = Diagnosis.builder()
                .id(id)
                .createdAt(Instant.now().minus(8, ChronoUnit.DAYS))
                .build();

        when(diagnosisRepository.findById(id)).thenReturn(Optional.of(d));

        AppException ex = assertThrows(AppException.class, () -> diagnoseService.getShared(id));
        assertTrue(ex.getMessage().contains("expired"));
    }

    @Test
    void getShared_ShouldThrowException_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(diagnosisRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> diagnoseService.getShared(id));
    }
}
