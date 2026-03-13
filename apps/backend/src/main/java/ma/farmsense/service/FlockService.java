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
        if (req.getSupplierId() != null) {
            supplier = supplierService.getOwned(user, req.getSupplierId());
        }

        Flock flock = Flock.builder()
                .user(user)
                .supplier(supplier)
                .name(req.getName())
                .nameAr(req.getNameAr())
                .nameEn(req.getNameEn())
                .breed(req.getBreed())
                .birdCount(req.getBirdCount())
                .currentBirdCount(req.getBirdCount()) // auto-set to birdCount
                .purpose(req.getPurpose())
                .startDate(req.getStartDate())
                .source(req.getSource())
                .notes(req.getNotes())
                .build();
        return FlockResponse.from(flockRepository.save(flock));
    }

    @Transactional
    public FlockResponse update(User user, UUID id, UpdateFlockRequest req) {
        Flock flock = getOwned(user, id);

        if (req.getSupplierId() != null) {
            Supplier supplier = supplierService.getOwned(user, req.getSupplierId());
            flock.setSupplier(supplier);
        }

        if (req.getName() != null) flock.setName(req.getName());
        if (req.getNameAr() != null) flock.setNameAr(req.getNameAr());
        if (req.getNameEn() != null) flock.setNameEn(req.getNameEn());
        if (req.getBreed() != null) flock.setBreed(req.getBreed());
        if (req.getBirdCount() != null) flock.setBirdCount(req.getBirdCount());
        if (req.getPurpose() != null) flock.setPurpose(req.getPurpose());
        if (req.getStatus() != null) flock.setStatus(req.getStatus());
        if (req.getStartDate() != null) flock.setStartDate(req.getStartDate());
        if (req.getSource() != null) flock.setSource(req.getSource());
        if (req.getNotes() != null) flock.setNotes(req.getNotes());

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
