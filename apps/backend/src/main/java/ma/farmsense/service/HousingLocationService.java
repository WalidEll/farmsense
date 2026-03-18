package ma.farmsense.service;

import ma.farmsense.dto.poultry.CreateHousingLocationRequest;
import ma.farmsense.dto.poultry.HousingLocationResponse;
import ma.farmsense.dto.poultry.UpdateHousingLocationRequest;
import ma.farmsense.entity.FlockStatus;
import ma.farmsense.entity.HousingLocation;
import ma.farmsense.entity.HousingLocationStatus;
import ma.farmsense.entity.HousingLocationType;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.FlockRepository;
import ma.farmsense.repository.HousingLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HousingLocationService {

    private final HousingLocationRepository housingLocationRepository;
    private final FlockRepository flockRepository;

    public HousingLocationService(HousingLocationRepository housingLocationRepository,
                                   FlockRepository flockRepository) {
        this.housingLocationRepository = housingLocationRepository;
        this.flockRepository = flockRepository;
    }

    public List<HousingLocationResponse> findAll(User user, HousingLocationType locationType) {
        List<HousingLocation> locations = locationType != null
                ? housingLocationRepository.findByUserAndLocationTypeOrderByCreatedAtDesc(user, locationType)
                : housingLocationRepository.findByUserOrderByCreatedAtDesc(user);
        return locations.stream()
                .map(l -> HousingLocationResponse.from(l, activeFlockCount(l.getId()), activeFlockName(l.getId())))
                .toList();
    }

    public HousingLocationResponse findById(User user, UUID id) {
        HousingLocation location = getOwned(user, id);
        return HousingLocationResponse.from(location, activeFlockCount(id), activeFlockName(id));
    }

    @Transactional
    public HousingLocationResponse create(User user, CreateHousingLocationRequest req) {
        HousingLocation location = new HousingLocation();
        location.setUser(user);
        location.setName(req.name());
        location.setNameAr(req.nameAr());
        location.setNameEn(req.nameEn());
        location.setLocationType(req.locationType());
        location.setStatus(req.status() != null ? req.status() : HousingLocationStatus.EMPTY);
        location.setCapacity(req.capacity() != null ? req.capacity() : 0);
        location.setNotes(req.notes());
        HousingLocation saved = housingLocationRepository.save(location);
        return HousingLocationResponse.from(saved, 0, null);
    }

    @Transactional
    public HousingLocationResponse update(User user, UUID id, UpdateHousingLocationRequest req) {
        HousingLocation location = getOwned(user, id);
        if (req.name() != null) location.setName(req.name());
        if (req.nameAr() != null) location.setNameAr(req.nameAr());
        if (req.nameEn() != null) location.setNameEn(req.nameEn());
        if (req.locationType() != null) location.setLocationType(req.locationType());
        if (req.status() != null) location.setStatus(req.status());
        if (req.capacity() != null) location.setCapacity(req.capacity());
        if (req.notes() != null) location.setNotes(req.notes());
        HousingLocation saved = housingLocationRepository.save(location);
        return HousingLocationResponse.from(saved, activeFlockCount(id), activeFlockName(id));
    }

    @Transactional
    public void delete(User user, UUID id) {
        HousingLocation location = getOwned(user, id);
        long activeFlocks = activeFlockCount(location.getId());
        if (activeFlocks > 0) {
            throw AppException.conflict(
                    "Cannot delete housing location with " + activeFlocks + " active flock(s) assigned. " +
                    "Reassign or retire all active flocks first.");
        }
        housingLocationRepository.delete(location);
    }

    public HousingLocation getOwned(User user, UUID id) {
        return housingLocationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> AppException.notFound("Housing location not found"));
    }

    private long activeFlockCount(UUID locationId) {
        return flockRepository.countByHousingLocationIdAndStatus(locationId, FlockStatus.ACTIVE);
    }

    private String activeFlockName(UUID locationId) {
        return flockRepository.findFirstByHousingLocationIdAndStatusOrderByCreatedAtDesc(locationId, FlockStatus.ACTIVE)
                .map(f -> f.getName())
                .orElse(null);
    }
}
