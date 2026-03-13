package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.poultry.CreateCustomerRequest;
import ma.farmsense.dto.poultry.CustomerResponse;
import ma.farmsense.dto.poultry.UpdateCustomerRequest;
import ma.farmsense.entity.Customer;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerResponse> findAll(User user, String search) {
        List<Customer> customers;
        if (search != null && !search.isBlank()) {
            customers = customerRepository.findByUserAndNameContainingIgnoreCaseOrderByNameAsc(user, search);
        } else {
            customers = customerRepository.findByUserOrderByNameAsc(user);
        }
        return customers.stream().map(CustomerResponse::from).toList();
    }

    public CustomerResponse findById(User user, UUID id) {
        return CustomerResponse.from(getOwned(user, id));
    }

    @Transactional
    public CustomerResponse create(User user, CreateCustomerRequest req) {
        Customer customer = Customer.builder()
                .user(user)
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .address(req.getAddress())
                .notes(req.getNotes())
                .build();
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(User user, UUID id, UpdateCustomerRequest req) {
        Customer customer = getOwned(user, id);
        if (req.getName() != null) customer.setName(req.getName());
        if (req.getPhone() != null) customer.setPhone(req.getPhone());
        if (req.getEmail() != null) customer.setEmail(req.getEmail());
        if (req.getAddress() != null) customer.setAddress(req.getAddress());
        if (req.getNotes() != null) customer.setNotes(req.getNotes());
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public void delete(User user, UUID id) {
        Customer customer = getOwned(user, id);
        customerRepository.delete(customer);
    }

    public Customer getOwned(User user, UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Customer not found"));
        if (!customer.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Customer not found");
        }
        return customer;
    }
}
