package kdaniel.customers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

class UserPrincipalTest {

    private Customer customer;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        // Arrange: Mock a Customer object
        customer = mock(Customer.class);
        when(customer.getUsername()).thenReturn("testuser");
        when(customer.getPassword()).thenReturn("password");
        Role role = new Role();
        role.setName("USER"); // Set the role to "USER"
        when(customer.getRole()).thenReturn(role);

        // Create UserPrincipal instance
        userPrincipal = new UserPrincipal(customer.getUsername(),null, role);
    }

    @Test
    void testGetAuthorities() {
        // Act: Get authorities from userPrincipal
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        // Assert: Check if authorities contain the correct role
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGetPassword() {
        // Act: Get password from userPrincipal
        String password = userPrincipal.getPassword();

        // Assert: Ensure password is correctly returned
        assertEquals("", password);
    }

    @Test
    void testGetUsername() {
        // Act: Get username from userPrincipal
        String username = userPrincipal.getUsername();

        // Assert: Ensure username is correctly returned
        assertEquals("testuser", username);
    }

    @Test
    void testIsAccountNonExpired() {
        // Act: Check if the account is non-expired
        boolean isNonExpired = userPrincipal.isAccountNonExpired();

        // Assert: It should return true
        assertTrue(isNonExpired);
    }

    @Test
    void testIsAccountNonLocked() {
        // Act: Check if the account is non-locked
        boolean isNonLocked = userPrincipal.isAccountNonLocked();

        // Assert: It should return true
        assertTrue(isNonLocked);
    }

    @Test
    void testIsCredentialsNonExpired() {
        // Act: Check if credentials are non-expired
        boolean isCredentialsNonExpired = userPrincipal.isCredentialsNonExpired();

        // Assert: It should return true
        assertTrue(isCredentialsNonExpired);
    }

    @Test
    void testIsEnabled() {
        // Act: Check if the account is enabled
        boolean isEnabled = userPrincipal.isEnabled();

        // Assert: It should return true
        assertTrue(isEnabled);
    }
}
