package kdaniel.customers.service;

import kdaniel.customers.dto.auth.RoleDTO;
import kdaniel.customers.dto.customer.CustomerDTO;
import kdaniel.customers.dto.customer.EditCustomerDTO;
import kdaniel.customers.dto.auth.JWTResponseDTO;
import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.model.Customer;
import kdaniel.customers.model.Role;
import kdaniel.customers.repository.CustomerRepository;
import kdaniel.customers.repository.RoleRepository;
import kdaniel.customers.util.FieldValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private CustomerService customerService;


    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private EditCustomerDTO editCustomerDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        // Initialize DTOs and Customer objects
        registerDTO = new RegisterDTO("user1", "password123", "Full Name", "test@example.com", "test@example.com", (byte) 25, RoleDTO.ADMIN);
        loginDTO = new LoginDTO("user1", "password123");
        editCustomerDTO = new EditCustomerDTO(1L, "user1", "new@example.com", "newPassword123", "etest@test.hu", (byte) 30);
        customer = new Customer(1L, "username", "test@example.com", "password123", (byte) 30, role);

        ModelMapper modelMapper = new ModelMapper();
        customerService = new CustomerService(customerRepository, roleRepository, jwtService, modelMapper, new BCryptPasswordEncoder());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("username");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    void testRegister() {
        // Arrange: Mock repository methods
        when(customerRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
        when(customerRepository.existsByUsername(registerDTO.getUsername())).thenReturn(false);
        when(roleRepository.findByName(String.valueOf(registerDTO.getRole()))).thenReturn(Optional.of(new Role("USER")));

        // Act: Call register method
        customerService.register(registerDTO);

        // Assert: Verify repository methods were called and customer was saved
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testRegister_ValidationFailure() {
        // Arrange: Set up a failed validation scenario
        when(customerRepository.existsByEmail(registerDTO.getEmail())).thenReturn(true);

        // Act & Assert: FieldValidationException should be thrown
        FieldValidationException exception = assertThrows(FieldValidationException.class, () -> {
            customerService.register(registerDTO);
        });
        assertTrue(exception.getErrors().containsKey("email"));
    }

    @Test
    public void testLogin_Success() {
        String password = "password123";
        String encodedPassword = new BCryptPasswordEncoder().encode(password); // Kódoljuk a jelszót

        Customer customer = new Customer(1L, "username","Full name", encodedPassword,  (byte) 30, new Role("ADMIN"));
        when(customerRepository.findByUsername("username")).thenReturn(Optional.of(customer));
        when(encoder.matches("password123", encodedPassword)).thenReturn(true); // Ellenőrizzük, hogy a jelszó megfelel

        LoginDTO loginDTO = new LoginDTO("username", "password123");

        try {
            JWTResponseDTO response = customerService.login(loginDTO);
            assertNotNull(response);
        } catch (FieldValidationException e) {
            fail("Login failed with valid credentials");
        }
    }

    @Test
    void testLogin_Failure() {
        // Arrange: Mock failed login scenario
        when(customerRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(customer));
        when(encoder.matches(loginDTO.getPassword(), customer.getPassword())).thenReturn(false);

        // Act & Assert: FieldValidationException should be thrown for bad password
        FieldValidationException exception = assertThrows(FieldValidationException.class, () -> {
            customerService.login(loginDTO);
        });
        assertTrue(exception.getErrors().containsKey("password"));
    }

    @Test
    void testGetAverageAge() {
        // Arrange: Mock repository stream
        Customer customer1 = new Customer();
        customer1.setAge((byte) 20);
        Customer customer2 = new Customer();
        customer2.setAge((byte) 40);
        when(customerRepository.streamAllCustomers()).thenReturn(Stream.of(customer, customer1, customer2));

        // Act: Call getAverageAge method
        Map<String, Double> result = customerService.getAverageAge();

        // Assert: Verify average age calculation
        assertEquals(30, result.get("averageAge"));
    }

    @Test
    void testGetAgeBetween18And40() {
        // Arrange: Mock repository method
        when(customerRepository.getCustomerBetween18And40()).thenReturn(
                List.of(new CustomerDTO("user1", (byte) 25, "user1@example.com"))
        );

        // Act: Call getAgeBetween18And40 method
        List<CustomerDTO> result = customerService.getAgeBetween18And40();

        // Assert: Verify the returned list
        assertFalse(result.isEmpty());
        assertEquals(25, result.get(0).getAge());
    }

    @Test
    void testEditCustomer() {
        // Arrange: Mock SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Map<String, String> token = new HashMap<>();
        token.put("newToken", "new-jwt-token");

        // Mock repository és JWT service
        when(customerRepository.findCustomerById(customer.getId())).thenReturn(Optional.ofNullable(customer));
        when(customerRepository.findCustomerByUsername(customer.getUsername())).thenReturn(Optional.ofNullable(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("new-jwt-token");

        // Act: Hívjuk meg az editCustomer metódust
        Map<String, String> result = customerService.editCustomer(editCustomerDTO);

        // Assert: Ellenőrizzük a token generálást és a mentést
        assertEquals("new-jwt-token", result.get("newToken"));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomer() {
        // Arrange: Mock repository methods
        when(customerRepository.existsById(customer.getId())).thenReturn(true);

        // Act: Call deleteCustomer method
        customerService.deleteCustomer(customer.getId());

        // Assert: Verify delete operation
        verify(customerRepository).deleteById(customer.getId());  // Verify it's called
    }

    @Test
    void testDeleteCustomer_NotFound() {
        // Arrange: Mock repository method to return false
        when(customerRepository.existsById(customer.getId())).thenReturn(false);

        // Act & Assert: FieldValidationException should be thrown
        FieldValidationException exception = assertThrows(FieldValidationException.class, () -> {
            customerService.deleteCustomer(customer.getId());
        });
        assertTrue(exception.getErrors().containsKey("user"));
    }
}

