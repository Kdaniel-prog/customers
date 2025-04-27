package kdaniel.customers.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kdaniel.customers.service.CustomerService;
import kdaniel.customers.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.PrintWriter;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private CustomerService customerService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtFilter = new JwtFilter(customerService, jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterWithoutAuthorizationHeader() throws Exception {
        // Arrange: Mock a request without Authorization header
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act: Call doFilterInternal
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert: Ensure that the filter chain is not interrupted
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterWithInvalidJwt() throws Exception {
        // Arrange: Mock a request with an invalid JWT token
        String invalidJwt = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidJwt);
        when(jwtService.extractUsername(invalidJwt)).thenReturn("testuser");

        UserDetails userDetails = mock(UserDetails.class);
        when(customerService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(invalidJwt, userDetails)).thenReturn(false);

        // Act: Call doFilterInternal
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert: Ensure that the filter chain is called and no authentication is set
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterWithValidJwt() throws Exception {
        // Arrange: Mock a request with a valid JWT token
        String validJwt = "valid.jwt.token";
        String username = "testuser";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validJwt);
        when(jwtService.extractUsername(validJwt)).thenReturn(username);

        // Mock CustomerService and JWTService
        UserDetails userDetails = mock(UserDetails.class);
        when(customerService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(validJwt, userDetails)).thenReturn(true);

        // Act: Call doFilterInternal
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert: Ensure that the user is authenticated in the security context
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterWithUsernameNotFound() throws Exception {
        // Arrange: Mock a request with a JWT token and a username that is not found
        String invalidJwt = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidJwt);
        when(jwtService.extractUsername(invalidJwt)).thenReturn("testuser");

        // Simulate a UsernameNotFoundException
        when(customerService.loadUserByUsername("testuser")).thenThrow(new UsernameNotFoundException("Username not found"));

        // Mock the PrintWriter for the response
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        // Act: Call doFilterInternal
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert: Ensure that the response status is 401 and error message is returned
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(response).getWriter();
        verify(writer).write(contains("Username not found"));
    }
}
