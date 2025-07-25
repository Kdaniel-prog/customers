package kdaniel.customers.service;

import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
/**
 * @Author Kiszel Dániel
 * @Date 2025-04-26
 * @Description Service class for handling JWT (JSON Web Token) generation, validation,
 *              and extraction of claims from the token. This service is used to authenticate
 *              and authorize users in the application by creating and validating JWT tokens.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JWTService {

    @Value("${jwt.secret}")
    String secretKey;

    @Value("${jwt.expiration}")
    Long secretExpiration;

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token The JWT token to extract the username from.
     * @return The username (subject) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token The JWT token to extract the claim from.
     * @param claimsResolver The function that retrieves the specific claim.
     * @param <T> The type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // Extract all claims first.
        if(claims == null) {
            return null;
        }
        return claimsResolver.apply(claims);  // Apply the provided function to get the specific claim.
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token to extract claims from.
     * @return The claims extracted from the token.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))  // Set the secret key for parsing.
                    .build()
                    .parseClaimsJws(token)  // Parse the JWT token.
                    .getBody();  // Get the body (claims) of the token.
        } catch (ExpiredJwtException e) {
            // Handle expired token
            System.out.println("The token is expired: " + e.getMessage());
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            // Handle other JWT-related exceptions (e.g., malformed token)
            System.out.println("Invalid token: " + e.getMessage());
            throw e;  // Optionally rethrow or handle invalid token logic
        }
    }

    /**
     * Validates whether the JWT token is still valid for the given user.
     *
     * @param token The JWT token to validate.
     * @param userDetails The user details for validation.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);  // Extract the username from the token.
        if(username == null) return false;
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);  // Validate by comparing username and checking expiration.
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token The JWT token to check for expiration.
     * @return True if the token has expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        try {
            Date expirationDate = extractExpiration(token);
            return expirationDate.before(new Date());
        } catch (Exception e) {
            System.out.println("Error checking token expiration: " + e.getMessage());
            return true;
        }
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token to extract the expiration from.
     * @return The expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);  // Extract the expiration claim from the token.
    }

    /**
     * Generates a new JWT token based on the user's details.
     *
     * @param userDetails The user details used to generate the token.
     * @return The generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty()) {
            claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());  // Add the user's role as a claim.
        } else {
            claims.put("role", "ROLE_USER");  // Set a default role
        }

        return Jwts.builder()
                .setClaims(claims)  // Set the claims in the token.
                .setSubject(userDetails.getUsername())  // Set the subject (username) of the token.
                .setIssuedAt(new Date())  // Set the issue date of the token.
                .setExpiration(new Date(System.currentTimeMillis() + secretExpiration))  // Set the expiration date of the token.
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)  // Sign the token using the secret key.
                .compact();  // Build and return the token.
    }
}
