package kdaniel.customers.controller;

import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.JWTResponseDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.service.CustomerService;
import kdaniel.customers.service.JWTService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private CustomerService customerService;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthController authController;

    private LoginDTO loginDTO;
    private RegisterDTO registerDTO;

    @BeforeEach
    public void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("testpassword");

        registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("testpassword");
        registerDTO.setConfirmEmail("test@test.com");
        registerDTO.setEmail("test@test.com");
        registerDTO.setRole("USER");
    }

    @Test
    public void testLogin_Success() {
        // Arrange: Mock the response from the customerService and jwtService
        String token = "mock-jwt-token";
        JWTResponseDTO jwtResponseDTO = new JWTResponseDTO(token);

        when(customerService.login(any(LoginDTO.class))).thenReturn(jwtResponseDTO);

        ResponseEntity<JWTResponseDTO> response = authController.login(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals(token, response.getBody().getToken());
    }

    @Test
    public void testLogin_Failure_InvalidCredentials() {
        // Arrange: Mock invalid credentials response
        when(customerService.login(any(LoginDTO.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        ResponseEntity<JWTResponseDTO> response = authController.login(loginDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRegister_Success() {
        // Arrange: Simulate successful registration (void method does nothing)
        doNothing().when(customerService).register(any(RegisterDTO.class));


        ResponseEntity<Void> response = authController.register(registerDTO);


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testRegister_Failure_UsernameExists() {
        // Arrange: Simulate failure where the username already exists
        Mockito.doThrow(new RuntimeException("Username already exists"))
                .when(customerService)
                .register(any(RegisterDTO.class));

        ResponseEntity<Void> response = authController.register(registerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
