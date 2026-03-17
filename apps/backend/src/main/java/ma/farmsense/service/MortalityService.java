package ma.farmsense.service;

import ma.farmsense.dto.poultry.CreateMortalityEventRequest;
import ma.farmsense.dto.poultry.MortalityEventCreateResponse;
import ma.farmsense.dto.poultry.MortalityEventResponse;
import ma.farmsense.entity.Flock;
import ma.farmsense.entity.FlockStatus;
import ma.farmsense.entity.MortalityRecord;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.MortalityRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MortalityService {

    private final MortalityRecordRepository mortalityRecordRepository;
    private final FlockService flockService;

    public MortalityService(MortalityRecordRepository mortalityRecordRepository,
                            FlockService flockService) {
        this.mortalityRecordRepository = mortalityRecordRepository;
        this.flockService = flockService;
    }

    @Transactional(readOnly = true)
    public List<MortalityEventResponse> findByFlock(User user, UUID flockId) {
        Flock flock = flockService.getOwned(user, flockId);
        return mortalityRecordRepository.findByFlockOrderByMortalityDateDesc(flock)
                .stream()
                .map(MortalityEventResponse::from)
                .toList();
    }

    @Transactional
    public MortalityEventCreateResponse create(User user, UUID flockId, CreateMortalityEventRequest req) {
        Flock flock = flockService.getOwnedWithLock(user, flockId);

        if (flock.getStatus() != FlockStatus.ACTIVE) {
            throw AppException.conflict(
                    "Cannot log mortality for a flock with status " + flock.getStatus() +
                    ". Only ACTIVE flocks accept new mortality events.");
        }

        if (req.count() > flock.getCurrentBirdCount()) {
            throw AppException.badRequest(
                    "Loss of " + req.count() + " exceeds current headcount of " +
                    flock.getCurrentBirdCount());
        }

        MortalityRecord record = new MortalityRecord();
        record.setFlock(flock);
        record.setType(req.type());
        record.setCount(req.count());
        record.setMortalityDate(req.mortalityDate());
        record.setCause(req.cause());
        record.setNotes(req.notes());
        mortalityRecordRepository.save(record);

        int updatedHeadcount = flock.getCurrentBirdCount() - req.count();
        flock.setCurrentBirdCount(updatedHeadcount);
        // FlockService.getOwned already loaded the managed entity; save via its own repo
        flockService.saveHeadcount(flock);

        return MortalityEventCreateResponse.from(record, updatedHeadcount);
    }
}
