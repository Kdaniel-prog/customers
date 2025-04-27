package kdaniel.customers.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    private JWTService jwtService;

    private final String secretKey = "33cc31f9a5eaa0402816925ceadfed4eeb19d900e5f55232253158ecd2cf865132e4566adccf4cf8202661088e8b8eeda7c9b46516754a2d42292d1aaa11917b9fd04e2881f86f39722ed5dace7ae204d1050cebb3346ce9f70ef380e351b04c5ffcdf3aa1cf5fe8293ea0fa9ba7129c96e9f92665533d7174b3fc804461db8e0bbfd942ce44ecff86640b2ded10b81c3359b203b53ad493458e86df50e9f5bdd33f182e521f5f659eb3bd624c219bbc220eec04b262a2960a0faa313308ca9f61132749bab77ff83e4b2cef1f83f54a5caabc0dc7fd34d784acdfaabeabf8fe6453469237581b891f6089fc23f6c9ff48820139cb576588be0e3febeb22e639";  // Actual secret key for testing
    private final Long expirationTime = 10000L;      // Expiration time in milliseconds (10 seconds)

    @BeforeEach
    void setUp() {
        // Create an instance of JWTService with actual values for testing
        jwtService = new JWTService(secretKey, expirationTime);
    }

    @Test
    void testGenerateToken() {
        // Arrange: Create mock userDetails
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());

        // Act: Generate token
        String token = jwtService.generateToken(userDetails);

        // Assert: Ensure the token is not null and contains username
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testuser", extractedUsername);
    }

    @Test
    void testIsTokenValid() {
        // Arrange: Create mock userDetails and generate token
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        // Act: Validate the token
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert: Ensure the token is valid
        assertTrue(isValid);
    }

    @Test
    void testIsTokenExpired() throws InterruptedException {
        // Arrange: Create mock userDetails and generate token
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        // Act: Wait for a sufficient amount of time to ensure the token has expired
        Thread.sleep(expirationTime + 5000); // Wait for enough time beyond the expiration

        // Act: Validate the token
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert: Ensure the token is expired
        assertFalse(isValid);
    }

    @Test
    void testExtractUsername() {
        // Arrange: Create mock userDetails and generate token
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        // Act: Extract username from token
        String extractedUsername = jwtService.extractUsername(token);

        // Assert: Ensure the extracted username matches the one in the token
        assertEquals("testuser", extractedUsername);
    }
}
