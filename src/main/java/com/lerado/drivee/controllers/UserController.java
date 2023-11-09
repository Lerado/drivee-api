package com.lerado.drivee.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lerado.drivee.dto.requests.AddRoleDto;
import com.lerado.drivee.entities.User;
import com.lerado.drivee.services.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(name = "users")
@RequiredArgsConstructor
public class UserController {

    final private UserService userService;

    /**
     * Add a role to a given user
     *
     * @param userId
     * @param payload
     * @return
     */
    @PutMapping(value = "{userId}/addRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addRole(@PathVariable long userId, @RequestBody AddRoleDto payload) {
        final User user = this.userService.findById(userId);
        user.addRole(payload.role());
        this.userService.update(user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a role to the current authenticated user
     *
     * @param payload
     * @return
     */
    @PutMapping(value = "authenticated/addRole")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> addRole(@RequestBody AddRoleDto payload) {
        final User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authenticatedUser.addRole(payload.role());
        this.userService.update(authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    
}
