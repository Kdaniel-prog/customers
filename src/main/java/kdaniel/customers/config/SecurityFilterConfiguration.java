package kdaniel.customers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author Kiszel Dániel
 * @Date 2025-04-26
 * @Description Configures the Spring Security filter chain.
 * Defines which endpoints are public and secures the rest using JWT authentication.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityFilterConfiguration {

    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityFilterConfiguration(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * @Bean
     * @Description Defines the security filter chain, disabling CSRF, configuring session management,
     * allowing public access to specific endpoints, and adding the JwtFilter.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since we are using JWT
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/auth/login", "/auth/register").permitAll()
                                // Allow access to H2 database console without authentication
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/member/**").permitAll()
                                .anyRequest().authenticated())
                // Allow H2 console frames
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
