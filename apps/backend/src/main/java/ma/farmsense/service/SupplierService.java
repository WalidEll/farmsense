package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.poultry.CreateSupplierRequest;
import ma.farmsense.dto.poultry.SupplierResponse;
import ma.farmsense.dto.poultry.UpdateSupplierRequest;
import ma.farmsense.entity.Supplier;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<SupplierResponse> findAll(User user, String search) {
        List<Supplier> suppliers;
        if (search != null && !search.isBlank()) {
            suppliers = supplierRepository.findByUserAndNameContainingIgnoreCaseOrderByNameAsc(user, search);
        } else {
            suppliers = supplierRepository.findByUserOrderByNameAsc(user);
        }
        return suppliers.stream().map(SupplierResponse::from).toList();
    }

    public SupplierResponse findById(User user, UUID id) {
        return SupplierResponse.from(getOwned(user, id));
    }

    @Transactional
    public SupplierResponse create(User user, CreateSupplierRequest req) {
        Supplier supplier = Supplier.builder()
                .user(user)
                .name(req.name())
                .phone(req.phone())
                .email(req.email())
                .address(req.address())
                .productsSupplied(req.productsSupplied())
                .notes(req.notes())
                .build();
        return SupplierResponse.from(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse update(User user, UUID id, UpdateSupplierRequest req) {
        Supplier supplier = getOwned(user, id);
        if (req.name() != null) supplier.setName(req.name());
        if (req.phone() != null) supplier.setPhone(req.phone());
        if (req.email() != null) supplier.setEmail(req.email());
        if (req.address() != null) supplier.setAddress(req.address());
        if (req.productsSupplied() != null) supplier.setProductsSupplied(req.productsSupplied());
        if (req.notes() != null) supplier.setNotes(req.notes());
        return SupplierResponse.from(supplierRepository.save(supplier));
    }

    @Transactional
    public void delete(User user, UUID id) {
        Supplier supplier = getOwned(user, id);
        supplierRepository.delete(supplier);
    }

    public Supplier getOwned(User user, UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Supplier not found"));
        if (!supplier.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Supplier not found");
        }
        return supplier;
    }
}
