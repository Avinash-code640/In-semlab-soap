
package com.klu.controller;

import com.klu.entity.User;
import com.klu.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public record RegisterRequest(
            @NotBlank(message = "Full name is required")
            @Size(max = 100)
            String fullName,

            @NotBlank(message = "Email is required")
            @Email(message = "Enter a valid email")
            @Size(max = 150)
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 8, max = 72,
                  message = "Password must be 8 to 72 characters")
            String password
    ) {}

    public record RegisterResponse(
            Long id,
            String fullName,
            String email,
            String message
    ) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        try {
            User user = userService.register(
                    request.fullName(),
                    request.email(),
                    request.password());

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new RegisterResponse(
                            user.getId(),
                            user.getFullName(),
                            user.getEmail(),
                            "Registration successful"));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
