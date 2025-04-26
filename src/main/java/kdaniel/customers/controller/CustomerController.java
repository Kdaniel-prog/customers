package kdaniel.customers.controller;

import kdaniel.customers.dto.customer.CustomerDTO;
import kdaniel.customers.model.Customer;
import kdaniel.customers.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
