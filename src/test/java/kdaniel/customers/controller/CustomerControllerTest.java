package kdaniel.customers.controller;

import kdaniel.customers.dto.customer.CustomerDTO;
import kdaniel.customers.dto.customer.EditCustomerDTO;
import kdaniel.customers.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private CustomerDTO customerDTO;
    private EditCustomerDTO editCustomerDTO;

    @BeforeEach
    public void setUp() {
        // Create mock CustomerDTO for GET and POST requests
        customerDTO = new CustomerDTO();
        customerDTO.setFullName("Test User");
        customerDTO.setAge((byte) 25);
        customerDTO.setEmail("test@test.com");

        // Create mock EditCustomerDTO for PUT request
        editCustomerDTO = new EditCustomerDTO();
        editCustomerDTO.setId(1L);
        editCustomerDTO.setUsername("testuser");
        editCustomerDTO.setFullName("Updated User");
        editCustomerDTO.setPassword("newpassword");
        editCustomerDTO.setEmail("updated@test.com");
        editCustomerDTO.setAge((byte) 26);
    }

    @Test
    public void testGetCustomerById_Success() {
        // Arrange: Mock the customerService to return a valid customer
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFullName("John Doe");
        customerDTO.setEmail("john.doe@example.com");
        customerDTO.setAge((byte) 30);

        when(customerService.getCustomer(1L)).thenReturn(customerDTO);

        // Act: Call the controller's getCustomerById method (or endpoint)
        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);

        // Assert: Check that status code is 200 and the customer data matches
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getFullName());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
        assertEquals(30, response.getBody().getAge());
    }

    @Test
    public void testGetCustomerById_NotFound() {
        // Arrange: Mock the customerService to return null for a non-existing customer
        when(customerService.getCustomer(999L)).thenReturn(null);

        // Act: Call the controller's getCustomerById method (or endpoint)
        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(999L);

        // Assert: Check that status code is 404 (Not Found)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testEditCustomer_ReturnsUpdatedDataWithNewToken() {
        // Mock the service method to return both updated customer data and a new token
        Map<String, String> responseMap = Map.of(
                "newToken", "new-token-after-edit"
        );

        // Mock the customerService.editCustomer method to return this map
        when(customerService.editCustomer(any(EditCustomerDTO.class)))
                .thenReturn(responseMap);

        // Act: Call the controller's editCustomer method (or endpoint)
        ResponseEntity<Map<String, String>> response = customerController.modifyCustomer(editCustomerDTO);

        // Assert: Verify status code and ensure the response contains the updated customer data and token
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("new-token-after-edit", response.getBody().get("newToken"));
    }

    @Test
    public void testGetAvarageAge() {
        // Arrange: Mock a response for average age
        Map<String, Integer> averageAge = new HashMap<>();
        averageAge.put("averageAge", 30);
        when(customerService.getAvarageAge()).thenReturn(averageAge);

        // Act: Call the controller method directly
        ResponseEntity<Map<String, Integer>> response = customerController.getAvarageAge();

        // Assert: Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(30, response.getBody().get("averageAge"));
    }

    @Test
    public void testGetBetween18And40_ReturnsListOfCustomers() {
        // Arrange: Mock a list of customers between 18 and 40
        List<CustomerDTO> customers = List.of(
                new CustomerDTO("John Doe", (byte) 25, "john@example.com"),
                new CustomerDTO("Jane Smith", (byte) 30, "jane@example.com")
        );
        when(customerService.getAgeBetween18And40()).thenReturn(customers);

        // Act: Call the controller's getBetween18And40 method
        ResponseEntity<List<CustomerDTO>> response = customerController.getBetween18And40();

        // Assert: Verify status code and contents
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getFullName());
        assertEquals("Jane Smith", response.getBody().get(1).getFullName());
    }

    @Test
    public void testDeleteCustomer_Success() {
        // Arrange: Mock the service method to delete the customer successfully
        Long customerId = 1L;

        // No need to mock a response, as deleteCustomer is void and does not return anything
        doNothing().when(customerService).deleteCustomer(customerId);

        // Act: Call the controller's deleteCustomer method
        ResponseEntity<Void> response = customerController.deleteCustomer(customerId);

        // Assert: Verify status code (should be 200 OK)
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify that the service's deleteCustomer method was called once with the correct ID
        verify(customerService, times(1)).deleteCustomer(customerId);
    }
}
