package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.accounting.*;
import ma.farmsense.entity.*;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LaborService {

    private final LaborLogRepository laborLogRepository;
    private final TransactionRepository transactionRepository;
    private final FlockRepository flockRepository;
    private final CropPlanRepository cropPlanRepository;
    private final FarmLocationRepository farmLocationRepository;

    public List<LaborLogResponse> findAll(User user) {
        return laborLogRepository.findByUserOrderByWorkDateDesc(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public LaborLogResponse findById(User user, UUID id) {
        LaborLog laborLog = getOwned(user, id);
        return mapToResponse(laborLog);
    }

    @Transactional
    public LaborLogResponse create(User user, CreateLaborLogRequest req) {
        BigDecimal totalAmount = req.hourlyRate().multiply(BigDecimal.valueOf(req.hoursWorked()));

        // Create auto-synced transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.EXPENSE)
                .category("Labor")
                .subcategory(req.workerRole())
                .amount(totalAmount)
                .quantity(req.hoursWorked())
                .unitPrice(req.hourlyRate())
                .transactionDate(req.workDate())
                .description("Labor: " + req.workerName() + " - " + req.activity())
                .approvalStatus(ApprovalStatus.APPROVED)
                .notes(req.notes())
                .build();

        updateTransactionRelations(transaction, req.flockId(), req.cropPlanId(), req.farmLocationId());
        Transaction savedTransaction = transactionRepository.save(transaction);

        LaborLog laborLog = LaborLog.builder()
                .user(user)
                .workerName(req.workerName())
                .workerRole(req.workerRole())
                .hourlyRate(req.hourlyRate())
                .hoursWorked(req.hoursWorked())
                .workDate(req.workDate())
                .activity(req.activity())
                .transaction(savedTransaction)
                .notes(req.notes())
                .build();

        updateLaborRelations(laborLog, req.flockId(), req.cropPlanId(), req.farmLocationId());
        
        return mapToResponse(laborLogRepository.save(laborLog));
    }

    @Transactional
    public LaborLogResponse update(User user, UUID id, UpdateLaborLogRequest req) {
        LaborLog laborLog = getOwned(user, id);

        if (req.workerName() != null) laborLog.setWorkerName(req.workerName());
        if (req.workerRole() != null) laborLog.setWorkerRole(req.workerRole());
        if (req.hourlyRate() != null) laborLog.setHourlyRate(req.hourlyRate());
        if (req.hoursWorked() != null) laborLog.setHoursWorked(req.hoursWorked());
        if (req.workDate() != null) laborLog.setWorkDate(req.workDate());
        if (req.activity() != null) laborLog.setActivity(req.activity());
        if (req.notes() != null) laborLog.setNotes(req.notes());

        updateLaborRelations(laborLog, req.flockId(), req.cropPlanId(), req.farmLocationId());

        // Update auto-synced transaction
        Transaction transaction = laborLog.getTransaction();
        if (transaction != null) {
            BigDecimal totalAmount = laborLog.getHourlyRate().multiply(BigDecimal.valueOf(laborLog.getHoursWorked()));
            transaction.setAmount(totalAmount);
            transaction.setQuantity(laborLog.getHoursWorked());
            transaction.setUnitPrice(laborLog.getHourlyRate());
            transaction.setTransactionDate(laborLog.getWorkDate());
            transaction.setSubcategory(laborLog.getWorkerRole());
            transaction.setDescription("Labor: " + laborLog.getWorkerName() + " - " + laborLog.getActivity());
            transaction.setNotes(laborLog.getNotes());
            
            updateTransactionRelations(transaction, req.flockId(), req.cropPlanId(), req.farmLocationId());
            transactionRepository.save(transaction);
        }

        return mapToResponse(laborLogRepository.save(laborLog));
    }

    @Transactional
    public void delete(User user, UUID id) {
        LaborLog laborLog = getOwned(user, id);
        Transaction transaction = laborLog.getTransaction();
        laborLogRepository.delete(laborLog);
        if (transaction != null) {
            transactionRepository.delete(transaction);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    private LaborLog getOwned(User user, UUID id) {
        LaborLog laborLog = laborLogRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Labor log not found"));
        if (!laborLog.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Labor log not found");
        }
        return laborLog;
    }

    private void updateLaborRelations(LaborLog laborLog, UUID flockId, UUID cropPlanId, UUID farmLocationId) {
        if (flockId != null) {
            laborLog.setFlock(flockRepository.findById(flockId).orElse(null));
        } else {
            laborLog.setFlock(null);
        }

        if (cropPlanId != null) {
            laborLog.setCropPlan(cropPlanRepository.findById(cropPlanId).orElse(null));
        } else {
            laborLog.setCropPlan(null);
        }

        if (farmLocationId != null) {
            laborLog.setFarmLocation(farmLocationRepository.findById(farmLocationId).orElse(null));
        } else {
            laborLog.setFarmLocation(null);
        }
    }

    private void updateTransactionRelations(Transaction transaction, UUID flockId, UUID cropPlanId, UUID farmLocationId) {
        if (flockId != null) {
            transaction.setFlock(flockRepository.findById(flockId).orElse(null));
        } else {
            transaction.setFlock(null);
        }

        if (cropPlanId != null) {
            transaction.setCropPlan(cropPlanRepository.findById(cropPlanId).orElse(null));
        } else {
            transaction.setCropPlan(null);
        }

        if (farmLocationId != null) {
            transaction.setFarmLocation(farmLocationRepository.findById(farmLocationId).orElse(null));
        } else {
            transaction.setFarmLocation(null);
        }
    }

    private LaborLogResponse mapToResponse(LaborLog l) {
        return new LaborLogResponse(
                l.getId(),
                l.getWorkerName(),
                l.getWorkerRole(),
                l.getHourlyRate(),
                l.getHoursWorked(),
                l.getWorkDate(),
                l.getActivity(),
                l.getTransaction() != null ? l.getTransaction().getId() : null,
                l.getFlock() != null ? l.getFlock().getId() : null,
                l.getFlock() != null ? l.getFlock().getName() : null,
                l.getCropPlan() != null ? l.getCropPlan().getId() : null,
                l.getCropPlan() != null ? l.getCropPlan().getName() : null,
                l.getFarmLocation() != null ? l.getFarmLocation().getId() : null,
                l.getFarmLocation() != null ? l.getFarmLocation().getName() : null,
                l.getNotes(),
                l.getCreatedAt(),
                l.getUpdatedAt()
        );
    }
}
