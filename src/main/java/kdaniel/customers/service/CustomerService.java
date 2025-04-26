package kdaniel.customers.service;

import kdaniel.customers.dto.customer.CustomerDTO;
import kdaniel.customers.model.Role;
import kdaniel.customers.repository.RoleRepository;
import org.springframework.transaction.annotation.Transactional;
import kdaniel.customers.dto.auth.JWTResponseDTO;
import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.model.Customer;
import kdaniel.customers.model.UserPrincipal;
import kdaniel.customers.repository.CustomerRepository;
import kdaniel.customers.util.FieldValidationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final JWTService jwtService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public CustomerService(
            CustomerRepository customerRepository,
            RoleRepository roleRepository,
            JWTService jwtService,
            ModelMapper modelMapper,
            BCryptPasswordEncoder encoder) {
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
    }

    public void register(RegisterDTO request) {
        Map<String, String> errors = new HashMap<>();

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

        Role role = roleRepository.findByName(request.getRole())
                .orElse(null);

        if (role == null) {
            errors.put("role", "Role not found");
        }

        if (!errors.isEmpty()) {
            throw new FieldValidationException(errors);
        }

        Customer user = modelMapper.map(request, Customer.class);
        user.setPassword(encoder.encode(request.getPassword()));
        customerRepository.save(user);
    }

    public JWTResponseDTO login(LoginDTO request) {
        Map<String, String> errors = new HashMap<>();

        Customer user = customerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    errors.put("username", "not found");
                    return new FieldValidationException(errors);
                });

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            errors.put("password", "bad password");
            throw new FieldValidationException(errors);
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );

        String token = jwtService.generateToken(userDetails);
        return new JWTResponseDTO(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getAvarageAge() {
        try (Stream<Customer> customerStream = customerRepository.streamAllCustomers()) {
            double avg = customerStream
                    .filter(c -> Objects.nonNull(c.getAge()))
                    .mapToDouble(Customer::getAge)
                    .average()
                    .orElse(0.0);

            Map<String, Integer> result = new HashMap<>();
            result.put("avarageAge", (int) Math.round(avg));
            return result;
        }
    }

    public List<CustomerDTO> getAgeBetween18And40() {
        return this.customerRepository.getCustomerBetween18And40();
    }
}
