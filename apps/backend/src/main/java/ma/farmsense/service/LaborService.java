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
        BigDecimal totalAmount = req.getHourlyRate().multiply(BigDecimal.valueOf(req.getHoursWorked()));
        
        // Create auto-synced transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.EXPENSE)
                .category("Labor")
                .subcategory(req.getWorkerRole())
                .amount(totalAmount)
                .quantity(req.getHoursWorked())
                .unitPrice(req.getHourlyRate())
                .transactionDate(req.getWorkDate())
                .description("Labor: " + req.getWorkerName() + " - " + req.getActivity())
                .approvalStatus(ApprovalStatus.APPROVED)
                .notes(req.getNotes())
                .build();
        
        updateTransactionRelations(transaction, req.getFlockId(), req.getCropPlanId(), req.getFarmLocationId());
        Transaction savedTransaction = transactionRepository.save(transaction);

        LaborLog laborLog = LaborLog.builder()
                .user(user)
                .workerName(req.getWorkerName())
                .workerRole(req.getWorkerRole())
                .hourlyRate(req.getHourlyRate())
                .hoursWorked(req.getHoursWorked())
                .workDate(req.getWorkDate())
                .activity(req.getActivity())
                .transaction(savedTransaction)
                .notes(req.getNotes())
                .build();
        
        updateLaborRelations(laborLog, req.getFlockId(), req.getCropPlanId(), req.getFarmLocationId());
        
        return mapToResponse(laborLogRepository.save(laborLog));
    }

    @Transactional
    public LaborLogResponse update(User user, UUID id, UpdateLaborLogRequest req) {
        LaborLog laborLog = getOwned(user, id);

        if (req.getWorkerName() != null) laborLog.setWorkerName(req.getWorkerName());
        if (req.getWorkerRole() != null) laborLog.setWorkerRole(req.getWorkerRole());
        if (req.getHourlyRate() != null) laborLog.setHourlyRate(req.getHourlyRate());
        if (req.getHoursWorked() != null) laborLog.setHoursWorked(req.getHoursWorked());
        if (req.getWorkDate() != null) laborLog.setWorkDate(req.getWorkDate());
        if (req.getActivity() != null) laborLog.setActivity(req.getActivity());
        if (req.getNotes() != null) laborLog.setNotes(req.getNotes());

        updateLaborRelations(laborLog, req.getFlockId(), req.getCropPlanId(), req.getFarmLocationId());

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
            
            updateTransactionRelations(transaction, req.getFlockId(), req.getCropPlanId(), req.getFarmLocationId());
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
        return LaborLogResponse.builder()
                .id(l.getId())
                .workerName(l.getWorkerName())
                .workerRole(l.getWorkerRole())
                .hourlyRate(l.getHourlyRate())
                .hoursWorked(l.getHoursWorked())
                .workDate(l.getWorkDate())
                .activity(l.getActivity())
                .transactionId(l.getTransaction() != null ? l.getTransaction().getId() : null)
                .flockId(l.getFlock() != null ? l.getFlock().getId() : null)
                .flockName(l.getFlock() != null ? l.getFlock().getName() : null)
                .cropPlanId(l.getCropPlan() != null ? l.getCropPlan().getId() : null)
                .cropPlanName(l.getCropPlan() != null ? l.getCropPlan().getName() : null)
                .farmLocationId(l.getFarmLocation() != null ? l.getFarmLocation().getId() : null)
                .farmLocationName(l.getFarmLocation() != null ? l.getFarmLocation().getName() : null)
                .notes(l.getNotes())
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .build();
    }
}
