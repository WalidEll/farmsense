package ma.farmsense.repository;

import ma.farmsense.entity.Supplier;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    List<Supplier> findByUserOrderByNameAsc(User user);

    List<Supplier> findByUserAndNameContainingIgnoreCaseOrderByNameAsc(User user, String name);
}
