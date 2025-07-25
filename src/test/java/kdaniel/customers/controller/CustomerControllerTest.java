package kdaniel.customers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdaniel.customers.dto.customer.EditCustomerDTO;
import kdaniel.customers.model.Customer;
import kdaniel.customers.model.Role;
import kdaniel.customers.repository.CustomerRepository;
import kdaniel.customers.repository.RoleRepository;
import kdaniel.customers.service.CustomerService;
import kdaniel.customers.service.JWTService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ModelMapper modelMapper;

    @MockitoBean
    JWTService jwtService;

    private final String BASE_URL = "/customer";

    EditCustomerDTO editCustomerDTO;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("USER")
                        .build()));

        Customer c1 = Customer.builder()
                .username("john")
                .password("{noop}password1")
                .fullName("John Doe")
                .email("john@example.com")
                .age((byte) 20)
                .role(userRole)
                .build();

        Customer c2 = Customer.builder()
                .username("user")
                .password("{noop}password2")
                .fullName("Jane Roe")
                .email("jane@example.com")
                .age((byte) 30)
                .role(userRole)
                .build();

        // Save both customers and get managed entities back
        List<Customer> savedCustomers = customerRepository.saveAll(List.of(c1, c2));

        Customer savedJane = savedCustomers.stream()
                .filter(c -> "user".equals(c.getUsername()))
                .findFirst()
                .orElseThrow();

        // Map saved entity to DTO AFTER it has ID
        editCustomerDTO = modelMapper.map(savedJane, EditCustomerDTO.class);

        editCustomerDTO.setFullName("Updated Jane");
        editCustomerDTO.setId(savedJane.getId());
    }

    @Test
    void shouldGetAverageAge_onCallAverage() throws Exception {
        mockMvc.perform(get(BASE_URL + "/averageAge")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageAge").value(25.0));
    }

    @Test
    void shouldAgeBetween18And40_onCallBetween18And40() throws Exception {
        mockMvc.perform(get(BASE_URL + "/between18And40")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnCustomer_onGetCustomerById() throws Exception {
        Customer user = customerRepository.findByUsername("john").get();

        mockMvc.perform(get(BASE_URL +"/" +user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value(user.getFullName()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnCustomers_onGetCustomers() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnNewToken_onModifyCustomer() throws Exception {
        // Mock jwtService to return "mocked-new-token" when called with any UserPrincipal
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mocked-new-token");

        mockMvc.perform(put(BASE_URL)
                        .content(new ObjectMapper().writeValueAsString(editCustomerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-new-token"));

        // check if full name changed
        mockMvc.perform(get(BASE_URL +"/" +editCustomerDTO.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value(editCustomerDTO.getFullName()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteCustomer_onDelete() throws Exception {
        Customer user = customerRepository.findByUsername("john").get();

        mockMvc.perform(delete(BASE_URL +"/" +user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
