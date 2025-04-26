package kdaniel.customers.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kdaniel.customers.service.CustomerService;
import kdaniel.customers.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Kiszel Dániel
 * @Date 2025-04-26
 * @Description JwtFilter for authenticating requests using JWT tokens.
 * Intercepts requests to validate the JWT and set the authentication context.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final CustomerService customerService;

    @Autowired
    public JwtFilter(CustomerService customerService, JWTService jwtService) {
        this.customerService = customerService;
        this.jwtService = jwtService;
    }

    /**
     * @Override
     * @Description Validates the JWT token in the Authorization header and sets the authentication context if valid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // If the Authorization header is missing or does not start with "Bearer ", continue the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT and username from the token
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        // If the username is valid and the authentication context is not already set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Load the user details using the customer service
                UserDetails userDetails = customerService.loadUserByUsername(username);

                // If the token is valid, set the authentication in the security context
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (UsernameNotFoundException ex) {
                // If the username is not found, respond with an error
                Map<String, String> errors = new HashMap<>();
                errors.put("username", ex.getMessage());

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String json = new ObjectMapper().writeValueAsString(errors);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
                return;
            }
        }

        // Continue the filter chain for other filters or final handling
        filterChain.doFilter(request, response);
    }
}
