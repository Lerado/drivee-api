package com.lerado.drivee.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.lerado.drivee.dto.requests.SignUpRequestDto;
import com.lerado.drivee.entities.RefreshToken;
import com.lerado.drivee.entities.User;
import com.lerado.drivee.exceptions.UserAlreadyExistsException;
import com.lerado.drivee.exceptions.UserNotFoundException;
import com.lerado.drivee.repositories.UserRepository;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        return this.repository.findByEmail(username).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User add(SignUpRequestDto userInfo) throws UserAlreadyExistsException {

        if (this.exists(userInfo.email()))
            throw new UserAlreadyExistsException();

        User user = new User();
        user.setName(userInfo.name());
        user.setEmail(userInfo.email());
        user.setPassword(new BCryptPasswordEncoder().encode(userInfo.password()));

        // Set refresh token
        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID().toString(),
                new Date(System.currentTimeMillis() + 1000 * 60 * 60));
        user.setUserRefreshToken(refreshToken);

        return this.repository.save(user);
    }

    @Override
    public User update(User user) throws UserNotFoundException {
        try {
            Assert.notNull(user.getId(), "User ID can not be null");
            return this.repository.save(user);
        } catch (IllegalArgumentException e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        return this.repository.findAll();
    }

    @Override
    public User findById(Long id) throws UserNotFoundException {
        return this.repository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    @Override
    public boolean exists(String email) {
        return this.repository.existsByEmail(email);
    }

    @Override
    public User findByRefreshToken(String refreshToken) throws UserNotFoundException {
        final List<User> queryResult = this.repository.findByUserRefreshToken_Token(refreshToken);
        if (queryResult.isEmpty())
            throw new UserNotFoundException();
        return queryResult.get(0);
    }

    @Override
    public User renewToken(User user) {
        user.renewToken();
        return this.repository.save(user);
    }
}
