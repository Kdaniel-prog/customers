package kdaniel.customers.service;

import kdaniel.customers.dto.auth.TokenDTO;
import kdaniel.customers.dto.customer.AverageAgeDTO;
import kdaniel.customers.dto.customer.CustomerDTO;
import kdaniel.customers.dto.customer.EditCustomerDTO;
import kdaniel.customers.model.ResponseModel;
import kdaniel.customers.model.Role;
import kdaniel.customers.validation.CustomerValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.model.Customer;
import kdaniel.customers.model.UserPrincipal;
import kdaniel.customers.repository.CustomerRepository;
import kdaniel.customers.util.FieldValidationException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

/**
 * @Author Kiszel Dániel
 * @Date 2025-04-26
 * @Description Service class for managing customer-related operations.
 * Handles registration, login, user authentication, and customer data processing.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService implements UserDetailsService {
    CustomerRepository customerRepository;
    JWTService jwtService;
    ModelMapper modelMapper;
    BCryptPasswordEncoder encoder;
    CustomerValidator customerValidator;

    /**
     * @Description Registers a new customer after validating the provided information.
     * @Param request The registration request containing user details.
     * @Throws FieldValidationException If validation fails.
     */
    public void validateAndSaveUser(RegisterDTO request) {
        Role role = customerValidator.validateRegisterDTO(request);

        Customer user = modelMapper.map(request, Customer.class);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(role);

        customerRepository.save(user);
    }

    /**
     * @Description Authenticates a user and generates a JWT token if credentials are valid.
     * @Param request The login request containing username and password.
     * @Return JWTResponseDTO containing the generated token.
     * @Throws FieldValidationException If authentication fails.
     */
    public ResponseModel<TokenDTO> validateUserAndReturnToken(LoginDTO request) {
        //validate data
        Customer user = customerValidator.validateLoginDTO(request);

        //generate token
        TokenDTO tokenDTO = generateToken(user);

        return new ResponseModel<>(true, tokenDTO);
    }

    /**
     * @Description Loads user details by username for Spring Security authentication.
     * @Param username The username to load.
     * @Return UserDetails containing user information.
     * @Throws FieldValidationException If the user is not found.
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) {
        return customerRepository.findByUsername(username)
                .map(customer -> new UserPrincipal(customer.getUsername(), customer.getRole()))
                .orElse(null);
    }

    /**
     * @Description Calculates and returns the average age of all customers.
     * @Return A map containing the average age.
     */
    @Transactional(readOnly = true)
    public ResponseModel<AverageAgeDTO> calculateAverageAge() {
        try (Stream<Customer> customerStream = customerRepository.streamAllCustomers()) {
            double avg = customerStream
                    .filter(c -> c.getAge() != null)
                    .mapToDouble(Customer::getAge)
                    .average()
                    .orElse(0.0);
            return new ResponseModel<>(true, new AverageAgeDTO(avg));
        } catch (Exception e) {
            throw new FieldValidationException("averageAge", "error");
        }
    }

    /**
     * @Description Returns a list of customers aged between 18 and 40.
     * @Return List of CustomerDTO objects.
     */
    @Transactional(readOnly = true)
    public ResponseModel<List<CustomerDTO>> getAgeBetween18And40() {
        return new ResponseModel<>(true, this.customerRepository.getCustomerBetween18And40());
    }

    /**
     * @Description Retrieves a customer's data by ID.
     * @Param id The customer's ID.
     * @Return CustomerDTO containing customer details.
     */
    @Transactional(readOnly = true)
    public ResponseModel<CustomerDTO> getCustomerById(Long id) {
        CustomerDTO customer = this.customerRepository.getCustomer(id);
        if(customer == null) {
            throw new FieldValidationException("id", "not found");
        }
        return new ResponseModel<>(true, customer);
    }

    /**
     * @Description Deletes a customer by their ID.
     * @Param id The customer's ID.
     * @Throws FieldValidationException If the customer does not exist.
     */
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new FieldValidationException("user", "not exist");
        }

        this.customerRepository.deleteById(id);
    }

    /**
     * @Description Edits an existing customer's details.
     * If the logged-in user's data changes, returns a new JWT token.
     * @Param editCustomerDTO The customer data to update.
     * @Return A map possibly containing a new JWT token.
     * @Throws FieldValidationException If validation fails.
     */
    public TokenDTO editCustomer(EditCustomerDTO editCustomerDTO) {
        //validate data
        customerValidator.validateEditDTO(editCustomerDTO);

        //Find customer
        Customer customer = this.customerRepository.findCustomerById(editCustomerDTO.getId()).get();

        //map new values
        modelMapper.map(editCustomerDTO, customer);
        customer.setPassword(encoder.encode(editCustomerDTO.getPassword()));

        //save
        this.customerRepository.save(customer);

        //return new token
        return generateToken(customer);
    }


    public ResponseModel<Page<CustomerDTO>> getAllCustomersPaged(Pageable pageable) {
        Page<CustomerDTO> customers = customerRepository.findAllCustomers(pageable)
                .map(customer -> modelMapper.map(customer, CustomerDTO.class));

        return new ResponseModel<>(true, customers);
    }

    private TokenDTO generateToken(Customer customer) {
        UserPrincipal userPrincipal = new UserPrincipal(
                customer.getUsername(),
                List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRole().getName())),
                customer.getRole()
        );
        return new TokenDTO(jwtService.generateToken(userPrincipal));
    }
}
