package kdaniel.customers.repository;

import jakarta.validation.constraints.NotBlank;
import kdaniel.customers.dto.customer.CustomerDTO;
import kdaniel.customers.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    List<Customer> findAll();
    Optional<Customer> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Customer findCustomerById(@NotBlank(message = "Id must not be empty") Long id);

    @Query("SELECT c FROM Customer c")
    Stream<Customer> streamAllCustomers();

    @Query("SELECT c.id, c.fullName, c.age, c.email FROM Customer c WHERE c.age >= 18 AND c.age <= 40")
    List<CustomerDTO> getCustomerBetween18And40();

    @Query("SELECT c.id, c.fullName, c.age, c.email FROM Customer c WHERE c.id = :id")
    CustomerDTO getCustomer(Long id);

}
