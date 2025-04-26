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

/**
 * @Author Kiszel Dániel
 * @Date 2025-04-26
 * @Description Controller for managing customers.
 * Handles requests for getting customer data, modifying customer details,
 * and deleting customers. Only accessible to ADMIN for sensitive actions.
 */

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * @GetMapping("/customer/avarageAge")
     * @Description Calculates and returns the average age of all customers.
     * @Return A map containing the average age.
     */
    @GetMapping("/avarageAge")
    public ResponseEntity<Map<String, Integer>> getAvarageAge() {
        return ResponseEntity.ok(customerService.getAvarageAge());
    }

    /**
     * @GetMapping("/customer/between18And40")
     * @Description Returns a list of customers whose age is between 18 and 40.
     * @Return List of CustomerDTOs.
     */
    @GetMapping("/between18And40")
    public ResponseEntity<List<CustomerDTO>> getBetween18And40() {
        return ResponseEntity.ok(customerService.getAgeBetween18And40());
    }

    /**
     * @GetMapping("/customer/{id}")
     * @Description Returns a customer's details by ID.
     * Only accessible by users with the ADMIN role.
     * @Param id - Customer ID.
     * @Return CustomerDTO.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(this.customerService.getCustomer(id));
    }

    /**
     * @PutMapping
     * @Description Modifies an existing customer based on the provided fields.
     * Only non-null fields will be updated.
     * Only accessible by users with the ADMIN role.
     * IF admin using his/her admin account than we will provide newToken
     * @Param customerDTO - Data for editing the customer.
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<Map<String, String>> modifyCustomer(@RequestBody EditCustomerDTO customerDTO) {
        return ResponseEntity.ok(this.customerService.editCustomer(customerDTO));
    }

    /**
     * @DeleteMapping
     * @Description Deletes a customer by ID.
     * Only accessible by users with the ADMIN role.
     * @Param id - Customer ID.
     */

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        this.customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

}
