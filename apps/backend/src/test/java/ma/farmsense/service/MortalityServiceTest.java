package ma.farmsense.service;

import ma.farmsense.dto.poultry.CreateMortalityEventRequest;
import ma.farmsense.dto.poultry.MortalityEventCreateResponse;
import ma.farmsense.dto.poultry.MortalityEventResponse;
import ma.farmsense.entity.*;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.MortalityRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MortalityServiceTest {

    @Mock
    private MortalityRecordRepository mortalityRecordRepository;
    @Mock
    private FlockService flockService;

    @InjectMocks
    private MortalityService mortalityService;

    private User testUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private Flock activeFlock(User user, int headcount) {
        Flock flock = new Flock();
        flock.setId(UUID.randomUUID());
        flock.setUser(user);
        flock.setBirdCount(headcount);
        flock.setCurrentBirdCount(headcount);
        flock.setStatus(FlockStatus.ACTIVE);
        flock.setName("Test Flock");
        flock.setPurpose(FlockPurpose.LAYERS);
        return flock;
    }

    @Test
    void create_ShouldDecrementHeadcount_OnSuccess() {
        User user = testUser();
        Flock flock = activeFlock(user, 500);
        UUID flockId = flock.getId();
        CreateMortalityEventRequest req = new CreateMortalityEventRequest(
                MortalityType.NATURAL_DEATH, 5, LocalDate.now(), MortalityCause.DISEASE, null);

        when(flockService.getOwnedWithLock(user, flockId)).thenReturn(flock);
        MortalityRecord saved = new MortalityRecord();
        saved.setId(UUID.randomUUID());
        saved.setFlock(flock);
        saved.setType(MortalityType.NATURAL_DEATH);
        saved.setCount(5);
        saved.setMortalityDate(LocalDate.now());
        saved.setCause(MortalityCause.DISEASE);
        when(mortalityRecordRepository.save(any())).thenReturn(saved);

        MortalityEventCreateResponse response = mortalityService.create(user, flockId, req);

        assertEquals(495, response.updatedFlockHeadcount());
        assertEquals(495, flock.getCurrentBirdCount());
        verify(flockService).saveHeadcount(flock);
    }

    @Test
    void create_ShouldThrowBadRequest_WhenCountExceedsHeadcount() {
        User user = testUser();
        Flock flock = activeFlock(user, 100);
        UUID flockId = flock.getId();
        CreateMortalityEventRequest req = new CreateMortalityEventRequest(
                MortalityType.NATURAL_DEATH, 150, LocalDate.now(), null, null);

        when(flockService.getOwnedWithLock(user, flockId)).thenReturn(flock);

        AppException ex = assertThrows(AppException.class,
                () -> mortalityService.create(user, flockId, req));
        assertEquals(400, ex.getStatus().value());
        verify(mortalityRecordRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowConflict_WhenFlockNotActive() {
        User user = testUser();
        Flock flock = activeFlock(user, 200);
        flock.setStatus(FlockStatus.SOLD);
        UUID flockId = flock.getId();
        CreateMortalityEventRequest req = new CreateMortalityEventRequest(
                MortalityType.CULL, 10, LocalDate.now(), MortalityCause.LOW_WEIGHT, null);

        when(flockService.getOwnedWithLock(user, flockId)).thenReturn(flock);

        AppException ex = assertThrows(AppException.class,
                () -> mortalityService.create(user, flockId, req));
        assertEquals(409, ex.getStatus().value());
        verify(mortalityRecordRepository, never()).save(any());
    }

    @Test
    void create_ShouldNotPersistRecord_WhenHeadcountSaveFails() {
        User user = testUser();
        Flock flock = activeFlock(user, 50);
        UUID flockId = flock.getId();
        CreateMortalityEventRequest req = new CreateMortalityEventRequest(
                MortalityType.CULL, 5, LocalDate.now(), MortalityCause.LOW_WEIGHT, null);

        when(flockService.getOwnedWithLock(user, flockId)).thenReturn(flock);
        doThrow(new RuntimeException("DB error")).when(flockService).saveHeadcount(any());

        assertThrows(RuntimeException.class,
                () -> mortalityService.create(user, flockId, req));
    }

    @Test
    void findByFlock_ShouldReturnEventList() {
        User user = testUser();
        Flock flock = activeFlock(user, 300);
        UUID flockId = flock.getId();
        MortalityRecord record = new MortalityRecord();
        record.setId(UUID.randomUUID());
        record.setFlock(flock);
        record.setType(MortalityType.NATURAL_DEATH);
        record.setCount(3);
        record.setMortalityDate(LocalDate.now());

        when(flockService.getOwned(user, flockId)).thenReturn(flock);
        when(mortalityRecordRepository.findByFlockOrderByMortalityDateDesc(flock)).thenReturn(List.of(record));

        List<MortalityEventResponse> result = mortalityService.findByFlock(user, flockId);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).count());
        assertEquals(MortalityType.NATURAL_DEATH, result.get(0).type());
    }
}
