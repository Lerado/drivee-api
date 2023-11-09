package com.lerado.drivee.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.lerado.drivee.dto.requests.SignUpRequestDto;
import com.lerado.drivee.entities.User;

@Service
public interface UserService extends UserDetailsService {

    /**
     * Checks if a user exists by a given email
     * 
     * @param email
     * @return
     */
    boolean exists(String email);

    /**
     * Add user into the database persistence
     *
     * @param {@link com.lerado.drivee.dto.requests.SignUpRequestDto} userInfos
     * @return {@link User} The persisted user
     * @throws com.lerado.drivee.exceptions.UserAlreadyExistsException
     */
    User add(SignUpRequestDto userInfos);

    /**
     * Update user into the database persistence
     *
     * @param com.lerado.drivee.dto.requests.SignUpRequestDto user
     * @return {@link User} The updated user entity
     */
    User update(User user);

    /**
     * Find all users
     *
     * @return The list of all users in the database
     */
    List<User> findAll();

    /**
     * Find a user by an id
     *
     * @param Long id
     * @return The resolved user
     * @throws com.lerado.drivee.exceptions.UserNotFoundException
     */
    User findById(Long id);

    /**
     * Find users using a refresh token
     *
     * @param refreshToken
     * @return The resolved user
     * @throws com.lerado.drivee.exceptions.UserNotFoundException
     */
    User findByRefreshToken(String refreshToken);

    /**
     * Renews the user's refesh token
     *
     * @param user
     * @return
     */
    User renewToken(User user);
}
