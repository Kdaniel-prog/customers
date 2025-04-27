package kdaniel.customers.controller;

import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.dto.auth.RoleDTO;
import kdaniel.customers.dto.auth.TokenDTO;
import kdaniel.customers.model.ResponseModel;
import kdaniel.customers.service.CustomerService;
import kdaniel.customers.service.JWTService;
import kdaniel.customers.util.FieldValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
        registerDTO.setRole(RoleDTO.USER);
    }

    @Test
    public void testLogin_Success() {
        // Arrange: Mock the response from the customerService and jwtService
        String token = "mock-jwt-token";
        ResponseModel<TokenDTO> responseModel = new ResponseModel<>(true, new TokenDTO(token));
        when(customerService.login(any(LoginDTO.class))).thenReturn(responseModel);

        ResponseEntity<ResponseModel<TokenDTO>> response = authController.login(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().getData().getToken());
    }

    @Test
    public void testLogin_Failure_InvalidCredentials() {
        // Arrange
        when(customerService.login(any(LoginDTO.class)))
                .thenThrow(new FieldValidationException("username", "Username must not be empty"));

        LoginDTO loginDTO = new LoginDTO("username", "wrongpassword");

        // Act + Assert
        FieldValidationException exception = assertThrows(FieldValidationException.class, () -> {
            authController.login(loginDTO);
        });

        assertNotNull(exception);
        assertTrue(exception.getErrors().containsKey("username"));
    }

    @Test
    public void testRegister_Success() {
        // Arrange: Simulate successful registration (void method does nothing)
        doNothing().when(customerService).register(any(RegisterDTO.class));
        ResponseEntity<Void> response = authController.register(registerDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testRegister_Failure_UsernameExists() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO(
                "existingUser", "password123", "Existing User",
                "user@example.com", "user@example.com", (byte) 25, RoleDTO.USER
        );

        Mockito.doThrow(new FieldValidationException("Username", "Username already exists"))
                .when(customerService)
                .register(any(RegisterDTO.class));

        // Act & Assert
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> authController.register(registerDTO)
        );

        assertTrue(exception.getErrors().containsKey("Username"));
    }

}
