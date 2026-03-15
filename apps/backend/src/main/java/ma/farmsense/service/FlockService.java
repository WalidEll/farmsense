package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.poultry.CreateFlockRequest;
import ma.farmsense.dto.poultry.FlockResponse;
import ma.farmsense.dto.poultry.UpdateFlockRequest;
import ma.farmsense.entity.*;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.FlockRepository;
import ma.farmsense.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlockService {

    private final FlockRepository flockRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierService supplierService;

    public List<FlockResponse> findAll(User user, FlockStatus status, FlockPurpose purpose) {
        List<Flock> flocks;
        if (status != null) {
            flocks = flockRepository.findByUserAndStatusOrderByCreatedAtDesc(user, status);
        } else if (purpose != null) {
            flocks = flockRepository.findByUserAndPurposeOrderByCreatedAtDesc(user, purpose);
        } else {
            flocks = flockRepository.findByUserOrderByCreatedAtDesc(user);
        }
        return flocks.stream().map(FlockResponse::from).toList();
    }

    public FlockResponse findById(User user, UUID id) {
        return FlockResponse.from(getOwned(user, id));
    }

    @Transactional
    public FlockResponse create(User user, CreateFlockRequest req) {
        Supplier supplier = null;
        if (req.supplierId() != null) {
            supplier = supplierService.getOwned(user, req.supplierId());
        }

        Flock flock = Flock.builder()
                .user(user)
                .supplier(supplier)
                .name(req.name())
                .nameAr(req.nameAr())
                .nameEn(req.nameEn())
                .breed(req.breed())
                .birdCount(req.birdCount())
                .currentBirdCount(req.birdCount()) // auto-set to birdCount
                .purpose(req.purpose())
                .startDate(req.startDate())
                .source(req.source())
                .notes(req.notes())
                .build();
        return FlockResponse.from(flockRepository.save(flock));
    }

    @Transactional
    public FlockResponse update(User user, UUID id, UpdateFlockRequest req) {
        Flock flock = getOwned(user, id);

        if (req.supplierId() != null) {
            Supplier supplier = supplierService.getOwned(user, req.supplierId());
            flock.setSupplier(supplier);
        }

        if (req.name() != null) flock.setName(req.name());
        if (req.nameAr() != null) flock.setNameAr(req.nameAr());
        if (req.nameEn() != null) flock.setNameEn(req.nameEn());
        if (req.breed() != null) flock.setBreed(req.breed());
        if (req.birdCount() != null) flock.setBirdCount(req.birdCount());
        if (req.purpose() != null) flock.setPurpose(req.purpose());
        if (req.status() != null) flock.setStatus(req.status());
        if (req.startDate() != null) flock.setStartDate(req.startDate());
        if (req.source() != null) flock.setSource(req.source());
        if (req.notes() != null) flock.setNotes(req.notes());

        return FlockResponse.from(flockRepository.save(flock));
    }

    @Transactional
    public void delete(User user, UUID id) {
        Flock flock = getOwned(user, id);
        // soft-delete: sets status to FINISHED
        flock.setStatus(FlockStatus.FINISHED);
        flockRepository.save(flock);
    }

    public Flock getOwned(User user, UUID id) {
        Flock flock = flockRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Flock not found"));
        if (!flock.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Flock not found");
        }
        return flock;
    }

    public long countActiveFlocks(User user) {
        return flockRepository.countByUserAndStatus(user, FlockStatus.ACTIVE);
    }
}
