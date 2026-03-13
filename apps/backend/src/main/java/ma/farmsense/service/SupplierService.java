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
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .address(req.getAddress())
                .productsSupplied(req.getProductsSupplied())
                .notes(req.getNotes())
                .build();
        return SupplierResponse.from(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse update(User user, UUID id, UpdateSupplierRequest req) {
        Supplier supplier = getOwned(user, id);
        if (req.getName() != null) supplier.setName(req.getName());
        if (req.getPhone() != null) supplier.setPhone(req.getPhone());
        if (req.getEmail() != null) supplier.setEmail(req.getEmail());
        if (req.getAddress() != null) supplier.setAddress(req.getAddress());
        if (req.getProductsSupplied() != null) supplier.setProductsSupplied(req.getProductsSupplied());
        if (req.getNotes() != null) supplier.setNotes(req.getNotes());
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
