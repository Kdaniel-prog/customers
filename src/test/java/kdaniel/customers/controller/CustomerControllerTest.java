package kdaniel.customers.controller;

import kdaniel.customers.dto.customer.AverageAgeDTO;
import kdaniel.customers.dto.customer.CustomerDTO;
import kdaniel.customers.dto.customer.EditCustomerDTO;
import kdaniel.customers.model.ResponseModel;
import kdaniel.customers.service.CustomerService;
import kdaniel.customers.util.FieldValidationException;
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

import static org.junit.jupiter.api.Assertions.*;
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

        when(customerService.getCustomer(1L)).thenReturn(new ResponseModel<>(true, customerDTO));

        // Act: Call the controller's getCustomerById method (or endpoint)
        ResponseEntity<ResponseModel<CustomerDTO>> response = customerController.getCustomerById(1L);

        // Assert: Check that status code is 200 and the customer data matches
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getData());
        assertEquals("John Doe", response.getBody().getData().getFullName());
        assertEquals("john.doe@example.com", response.getBody().getData().getEmail());
        assertEquals(30, response.getBody().getData().getAge());
    }

    @Test
    public void testGetCustomerById_NotFound() {
        when(customerService.getCustomer(999L)).thenThrow(new FieldValidationException("id", "not found"));

        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> customerController.getCustomerById(999L)
        );

        assertTrue(exception.getErrors().containsKey("id"));
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
    public void testGetAverageAge_ReturnAverageAge() {
        // Arrange: Mock a response for average age
        ResponseModel<AverageAgeDTO> responseModel = new ResponseModel<>(true, new AverageAgeDTO(30.0));
        when(customerService.getAverageAge()).thenReturn(responseModel);

        // Act: Call the controller method directly
        ResponseEntity<ResponseModel<AverageAgeDTO>> response = customerController.getAverageAge();

        // Assert: Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(30.0, response.getBody().getData().getAverageAge());
    }

    @Test
    public void testGetBetween18And40_ReturnsListOfCustomers() {
        // Arrange: Mock a list of customers between 18 and 40
        List<CustomerDTO> customers = List.of(
                new CustomerDTO("John Doe", (byte) 25, "john@example.com"),
                new CustomerDTO("Jane Smith", (byte) 30, "jane@example.com")
        );
        ResponseModel<List<CustomerDTO>> responseModel = new ResponseModel<>(true, customers);
        when(customerService.getAgeBetween18And40()).thenReturn(responseModel);

        // Act: Call the controller's getBetween18And40 method
        ResponseEntity<ResponseModel<List<CustomerDTO>>> response = customerController.getBetween18And40();

        // Assert: Verify status code and contents
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getData().size());
        assertEquals("John Doe", response.getBody().getData().get(0).getFullName());
        assertEquals("Jane Smith", response.getBody().getData().get(1).getFullName());
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
