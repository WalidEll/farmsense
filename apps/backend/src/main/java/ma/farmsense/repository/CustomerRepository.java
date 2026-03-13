package ma.farmsense.repository;

import ma.farmsense.entity.Customer;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findByUserOrderByNameAsc(User user);

    List<Customer> findByUserAndNameContainingIgnoreCaseOrderByNameAsc(User user, String name);
}
