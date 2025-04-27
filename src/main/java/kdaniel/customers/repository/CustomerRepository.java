package kdaniel.customers.repository;

import jakarta.validation.constraints.NotBlank;
import kdaniel.customers.dto.customer.CustomerDTO;
import kdaniel.customers.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<Customer> findCustomerById(@NotBlank(message = "Id must not be empty") Long id);

    @Query("SELECT c FROM Customer c")
    Stream<Customer> streamAllCustomers();

    @NativeQuery("SELECT FULL_NAME, AGE, EMAIL FROM Customer WHERE age >= 18 AND age <= 40")
    List<CustomerDTO> getCustomerBetween18And40();

    @NativeQuery("SELECT FULL_NAME, AGE, EMAIL FROM Customer WHERE id =:id")
    CustomerDTO getCustomer(Long id);

    Optional<Customer> findCustomerByUsername(String currentUsername);
}
