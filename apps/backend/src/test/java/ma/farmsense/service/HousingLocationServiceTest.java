package ma.farmsense.service;

import ma.farmsense.dto.poultry.CreateHousingLocationRequest;
import ma.farmsense.dto.poultry.HousingLocationResponse;
import ma.farmsense.dto.poultry.UpdateHousingLocationRequest;
import ma.farmsense.entity.FlockStatus;
import ma.farmsense.entity.HousingLocation;
import ma.farmsense.entity.HousingLocationType;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.FlockRepository;
import ma.farmsense.repository.HousingLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HousingLocationServiceTest {

    @Mock
    private HousingLocationRepository housingLocationRepository;
    @Mock
    private FlockRepository flockRepository;

    @InjectMocks
    private HousingLocationService housingLocationService;

    private User testUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private HousingLocation testLocation(User user) {
        HousingLocation loc = new HousingLocation();
        loc.setId(UUID.randomUUID());
        loc.setUser(user);
        loc.setName("Coop A");
        loc.setLocationType(HousingLocationType.COOP);
        return loc;
    }

    @Test
    void create_ShouldSaveAndReturnResponse() {
        User user = testUser();
        CreateHousingLocationRequest req = new CreateHousingLocationRequest(
                "Coop A", null, null, HousingLocationType.COOP, null);
        HousingLocation saved = testLocation(user);
        when(housingLocationRepository.save(any())).thenReturn(saved);

        HousingLocationResponse response = housingLocationService.create(user, req);

        assertEquals("Coop A", response.name());
        assertEquals(HousingLocationType.COOP, response.locationType());
        verify(housingLocationRepository).save(any());
    }

    @Test
    void delete_ShouldSucceed_WhenNoActiveFlocks() {
        User user = testUser();
        HousingLocation loc = testLocation(user);
        when(housingLocationRepository.findByIdAndUser(loc.getId(), user)).thenReturn(Optional.of(loc));
        when(flockRepository.countByHousingLocationIdAndStatus(loc.getId(), FlockStatus.ACTIVE)).thenReturn(0L);

        assertDoesNotThrow(() -> housingLocationService.delete(user, loc.getId()));
        verify(housingLocationRepository).delete(loc);
    }

    @Test
    void delete_ShouldThrowConflict_WhenActiveFlockExists() {
        User user = testUser();
        HousingLocation loc = testLocation(user);
        when(housingLocationRepository.findByIdAndUser(loc.getId(), user)).thenReturn(Optional.of(loc));
        when(flockRepository.countByHousingLocationIdAndStatus(loc.getId(), FlockStatus.ACTIVE)).thenReturn(2L);

        AppException ex = assertThrows(AppException.class,
                () -> housingLocationService.delete(user, loc.getId()));
        assertEquals(409, ex.getStatus().value());
        verify(housingLocationRepository, never()).delete(any());
    }

    @Test
    void findById_ShouldThrowNotFound_WhenNotOwned() {
        User user = testUser();
        UUID id = UUID.randomUUID();
        when(housingLocationRepository.findByIdAndUser(id, user)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> housingLocationService.findById(user, id));
        assertEquals(404, ex.getStatus().value());
    }

    @Test
    void update_ShouldUpdateFields_WhenProvided() {
        User user = testUser();
        HousingLocation loc = testLocation(user);
        UUID id = loc.getId();
        UpdateHousingLocationRequest req = new UpdateHousingLocationRequest("Pen B", null, null, HousingLocationType.PEN, "Updated");
        when(housingLocationRepository.findByIdAndUser(id, user)).thenReturn(Optional.of(loc));
        when(housingLocationRepository.save(any())).thenReturn(loc);
        when(flockRepository.countByHousingLocationIdAndStatus(id, FlockStatus.ACTIVE)).thenReturn(0L);

        HousingLocationResponse response = housingLocationService.update(user, id, req);

        assertEquals("Pen B", loc.getName());
        assertEquals(HousingLocationType.PEN, loc.getLocationType());
    }

    @Test
    void findAll_ShouldReturnFilteredList_ByType() {
        User user = testUser();
        HousingLocation loc = testLocation(user);
        when(housingLocationRepository.findByUserAndLocationTypeOrderByCreatedAtDesc(user, HousingLocationType.COOP))
                .thenReturn(List.of(loc));
        when(flockRepository.countByHousingLocationIdAndStatus(loc.getId(), FlockStatus.ACTIVE)).thenReturn(1L);

        List<HousingLocationResponse> result = housingLocationService.findAll(user, HousingLocationType.COOP);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).currentFlockCount());
    }
}
