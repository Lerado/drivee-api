package com.lerado.drivee.repositories;

import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import com.lerado.drivee.entities.User;

import java.util.List;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {

    /**
     * Find user with email as query
     *
     * @param name
     * @return {@link com.lerado.drivee.entities.User}
     */
    public Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists by a given email
     * 
     * @param email
     * @return
     */
    public boolean existsByEmail(String email);

    List<User> findByUserRefreshToken_Token(String token);
}
