package com.lerado.drivee.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lerado.drivee.entities.User;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "greeting")
@AllArgsConstructor
class GreetingController {

    /**
     * Produces a greeting message using the name of the user
     *
     * @param name
     * @return {@link String} The personalized greeting message
     */
    @GetMapping(path = "")
    public String greet(@RequestParam(name = "name", defaultValue = "World") String name) {
        return String.format("Hello %s !", name);
    }

    /**
     * Greet authenticated users
     * 
     * @return
     */
    @GetMapping("auth")
    @PreAuthorize("isAuthenticated()")
    public String greetAutenticatedUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return String.format("Hello %s !", user.getName());
    }

    /**
     * Greet authenticated users who are administrators
     * 
     * @return
     */
    @GetMapping("auth/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String greetAdminUser() {
        return this.greetAutenticatedUser();
    }
}