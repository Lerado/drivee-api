package com.lerado.drivee.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public interface JwtService {

    /**
     * Generates a valid JWT token based on username
     * 
     * @param username
     * @return
     */
    public String generateToken(String username);

    /**
     * Extracts the username encapsulated in a JWT token
     *
     * @param jwtToken
     * @return
     */
    public String extractUsername(String jwtToken);

    /**
     * Checks token validity
     *
     * @param token
     * @param user
     * @return
     */
    public boolean validateToken(String token, UserDetails user);
}
