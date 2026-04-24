package hr.algebra.voyabackend.security;

import hr.algebra.voyabackend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtUtilities {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpiration;

    /**
     * Generate JWT token
     * @param user User
     * @return JWT token as String
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the provided JWT token by validating the email and expiration
     * @param token JWT token
     * @param email User email to validate email
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    /**
     * Checks if the provided JWT token is expired
     * @param token JWT token
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    /**
     * Extracts the email from the provided JWT token
     * @param token JWT token
     * @return User email
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extracts the role from the provided JWT token
     * @param token JWT token
     * @return User role
     */
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * Helper function to parse claims from a JWT token
     * @param token JWT token
     * @return Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
