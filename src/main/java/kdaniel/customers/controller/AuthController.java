package kdaniel.customers.controller;

import jakarta.validation.Valid;
import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.dto.auth.TokenDTO;
import kdaniel.customers.model.ResponseModel;
import kdaniel.customers.service.CustomerService;
import kdaniel.customers.util.FieldValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public ResponseEntity<Void> register(@RequestBody RegisterDTO registerDTO) {
        service.register(registerDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseModel<TokenDTO>> login(@Valid @RequestBody LoginDTO user) {
            return ResponseEntity.ok(service.login(user));
    }

}
