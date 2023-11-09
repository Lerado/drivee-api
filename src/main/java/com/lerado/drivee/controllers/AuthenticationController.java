package com.lerado.drivee.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lerado.drivee.dto.requests.AuthenticationRequestDto;
import com.lerado.drivee.dto.requests.RefreshTokenRequestDto;
import com.lerado.drivee.dto.requests.SignUpRequestDto;
import com.lerado.drivee.dto.responses.AuthenticationResponseDto;
import com.lerado.drivee.entities.User;
import com.lerado.drivee.exceptions.AuthenticationFailedException;
import com.lerado.drivee.exceptions.RefreshTokenExpiredException;
import com.lerado.drivee.services.JwtService;
import com.lerado.drivee.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "oauth")
@RequiredArgsConstructor
public class AuthenticationController {

    final private UserService userService;

    final private JwtService jwtService;

    /**
     * Authentication manager
     * 
     * As {@link org.springframework.security.authentication.AuthenticationManager}
     * is not decorated component,
     * we need to provide a bean for it in
     * {@link com.lerado.drivee.config.JwtSecurityConfig}
     */
    final private AuthenticationManager authenticationManager;

    /**
     * Sends a JWT token to the user if authentication is successful
     *
     * @param request
     * @return
     */
    @PostMapping(path = "login")
    public ResponseEntity<AuthenticationResponseDto> token(@RequestBody AuthenticationRequestDto request) {

        try {
            final Authentication authentication = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            
            User authenticatedUser = (User) authentication.getPrincipal();

            // Renew refresh token
            authenticatedUser = this.userService.renewToken(authenticatedUser);

            return ResponseEntity.ok(
                    new AuthenticationResponseDto(
                            this.jwtService.generateToken(authenticatedUser.getUsername()), authenticatedUser.getRefreshToken()));
        } catch (Exception e) {
            throw new AuthenticationFailedException(e.getMessage());
        }
    }

    /**
     * User sign up
     *
     * @param request
     * @return
     */
    @PostMapping(path = "sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto request) {
        this.userService.add(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Get current authenticated user
     *
     * @return
     */
    @GetMapping(path = "authenticated")
    @PreAuthorize("isAuthenticated()")
    public User authenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Refresh token strategy
     *
     * @param refreshToken
     * @return
     */
    @PostMapping(path = "refresh")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request) {

        User user = this.userService.findByRefreshToken(request.refreshToken());

        if (user.isTokenExpired()) {
            throw new RefreshTokenExpiredException();
        }

        // Revoke refresh token and persist change
        user = this.userService.renewToken(user);

        AuthenticationResponseDto authenticationResponse = new AuthenticationResponseDto(
            this.jwtService.generateToken(user.getUsername()),
            user.getRefreshToken());

        return ResponseEntity.ok(authenticationResponse);
    }
}
