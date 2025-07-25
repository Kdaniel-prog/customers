package kdaniel.customers.controller;

import jakarta.validation.Valid;
import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.dto.auth.TokenDTO;
import kdaniel.customers.model.ResponseModel;
import kdaniel.customers.service.CustomerService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AuthController {
    CustomerService service;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        service.validateAndSaveUser(registerDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseModel<TokenDTO>> login(@Valid @RequestBody LoginDTO user) {
            return ResponseEntity.ok(service.validateUserAndReturnToken(user));
    }

}
