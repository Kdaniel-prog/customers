package kdaniel.customers.controller;

import kdaniel.customers.util.FieldValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testHandleFieldValidationException() {
        // Arrange: mock egy FieldValidationException-t
        Map<String, String> errors = new HashMap<>();
        errors.put("field1", "Field1 is required.");
        errors.put("field2", "Field2 must be a valid email.");

        FieldValidationException ex = new FieldValidationException(errors);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleFieldValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Field1 is required.", response.getBody().get("field1"));
        assertEquals("Field2 must be a valid email.", response.getBody().get("field2"));
    }

    @Test
    public void testHandleMethodArgumentNotValidException() {
        FieldError error1 = new FieldError("object", "username", "Username is required.");
        FieldError error2 = new FieldError("object", "password", "Password must be at least 6 characters.");

        List<FieldError> fieldErrors = List.of(error1, error2);

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("Username is required.", response.getBody().get("username"));
        assertEquals("Password must be at least 6 characters.", response.getBody().get("password"));
    }
}
