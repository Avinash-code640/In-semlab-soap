
package com.klu.service;

import com.klu.entity.User;
import com.klu.repository.UserRepository;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String fullName, String email, String password) {

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (repository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        User user = new User(
                fullName.trim(),
                normalizedEmail,
                passwordEncoder.encode(password));

        try {
            return repository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }
    }
}
