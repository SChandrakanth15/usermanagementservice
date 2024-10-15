package com.theelixrlabs.UserManagementService.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token JWT token.
     * @return Username extracted from the token.
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract a specific claim from the JWT token.
     *
     * @param token JWT token.
     * @param claimResolver A function to resolve a specific claim from Claims.
     * @param <T> Type of the claim.
     * @return The claim extracted.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Extract all claims from the JWT token.
     *
     * @param token JWT token.
     * @return All claims present in the token.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey()) // Use the decoded secret key
                    .build()
                    .parseClaimsJws(token)    // Parse the JWT token and retrieve its claims
                    .getBody();
        } catch (Exception e) {
            logger.error("Error extracting claims from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }

    /**
     * Validates if the token is still valid (i.e., not expired).
     *
     * @param token JWT token.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token JWT token.
     * @return true if the token has expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token JWT token.
     * @return Expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Returns the signing key by decoding the secret key provided in the configuration.
     *
     * @return Key to be used for signing the JWT.
     */
    private Key getKey() {
        // Decode the Base64-encoded secret key
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

