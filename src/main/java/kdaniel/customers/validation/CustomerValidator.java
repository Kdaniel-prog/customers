package kdaniel.customers.validation;

import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.model.Customer;
import kdaniel.customers.model.Role;
import kdaniel.customers.model.UserPrincipal;
import kdaniel.customers.repository.CustomerRepository;
import kdaniel.customers.repository.RoleRepository;
import kdaniel.customers.util.FieldValidationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class CustomerValidator {
    CustomerRepository customerRepository;
    RoleRepository roleRepository;
    BCryptPasswordEncoder encoder;

    public Customer validateLoginDTO(LoginDTO request) {
        Customer user = customerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new FieldValidationException("username", "not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new FieldValidationException("password", "bad password");
        }

        return user;
    }

    public Role validateRegisterDTO(RegisterDTO request) {
        Map<String, String> errors = new HashMap<>();

        Role role = roleRepository.findByName(String.valueOf(request.getRole()))
                .orElse(null);

        if (role == null) {
            errors.put("role", "Role not found");
        }

        if (!request.getEmail().equals(request.getConfirmEmail())) {
            errors.put("confirmEmail", "Emails do not match");
        }

        if (customerRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "Email is already taken");
        }

        if (customerRepository.existsByUsername(request.getUsername())) {
            errors.put("username", "Username already exists");
        }

        if (!errors.isEmpty()) {
            throw new FieldValidationException(errors);
        }

        return role;
    }
}
