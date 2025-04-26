package kdaniel.customers.controller;

import jakarta.validation.Valid;
import kdaniel.customers.dto.auth.JWTResponseDTO;
import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: for user registration and login.
 * Handles requests for registering and logging in users.
 *
 * @Author: Kiszel Dániel
 * @Date: 2025-04-26
 */

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final CustomerService service;

    @Autowired
    public AuthController(CustomerService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterDTO user) {
        service.register(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponseDTO> login(@Valid @RequestBody LoginDTO user) {
        JWTResponseDTO token = service.login(user);
        return ResponseEntity.ok(token);
    }

}
