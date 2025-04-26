package kdaniel.customers.controller;

import kdaniel.customers.dto.customer.CustomerDTO;
import kdaniel.customers.dto.customer.EditCustomerDTO;
import kdaniel.customers.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/avarageAge")
    public ResponseEntity<Map<String, Integer>> getAvarageAge() {
        return ResponseEntity.ok(customerService.getAvarageAge());
    }

    @GetMapping("/between18And40")
    public ResponseEntity<List<CustomerDTO>> getBetween18And40() {
        return ResponseEntity.ok(customerService.getAgeBetween18And40());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(this.customerService.getCustomer(id));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> modifyCustomer(@RequestBody EditCustomerDTO customerDTO) {
        this.customerService.editCustomer(customerDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        this.customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

}
