package kdaniel.customers.controller;

import kdaniel.customers.model.Customer;
import kdaniel.customers.model.Role;
import kdaniel.customers.repository.CustomerRepository;
import kdaniel.customers.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    RoleRepository roleRepository;

    private final String BASE_URL = "/customer";

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
                .username("jane")
                .password("{noop}password2")
                .fullName("Jane Roe")
                .email("jane@example.com")
                .age((byte) 30)
                .role(userRole)
                .build();

        customerRepository.saveAll(List.of(c1, c2));
    }

    @Test
    void shouldGetAverageAge_onCallAverage() throws Exception {
        mockMvc.perform(get(BASE_URL + "/averageAge")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageAge").value(25.0));
    }
}
