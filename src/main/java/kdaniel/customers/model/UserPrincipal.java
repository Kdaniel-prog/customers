package kdaniel.customers.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of the UserDetails interface for Spring Security.
 * This class is used to wrap a Customer object and provide necessary user details for authentication and authorization.
 *
 * @Author Kiszel Dániel
 * @Date 2025-04-26
 */
@Getter
@Setter
public class UserPrincipal implements UserDetails {

    private Role role;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(String username, Collection<? extends GrantedAuthority> authorities, Role role) {
        this.username = username;
        this.authorities = authorities;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}