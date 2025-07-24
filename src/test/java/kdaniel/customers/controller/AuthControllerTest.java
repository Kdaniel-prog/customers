package kdaniel.customers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdaniel.customers.dto.auth.LoginDTO;
import kdaniel.customers.dto.auth.RegisterDTO;
import kdaniel.customers.dto.auth.RoleDTO;
import kdaniel.customers.dto.auth.TokenDTO;
import kdaniel.customers.model.ResponseModel;
import kdaniel.customers.model.Role;
import kdaniel.customers.model.UserPrincipal;
import kdaniel.customers.service.CustomerService;
import kdaniel.customers.service.JWTService;
import kdaniel.customers.util.FieldValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomerService customerService;

    @MockitoBean
    JWTService jwtService;

    RegisterDTO registerDTO;
    LoginDTO loginDTO;

    private final static String REGISTER_URL = "/auth/register";
    private final static String LOGIN_URL = "/auth/login";

    @BeforeEach
    void setUp() {
        registerDTO = RegisterDTO.builder()
                .username("testUser")
                .password("testPassword")
                .email("test@test.hu")
                .confirmEmail("test@test.hu")
                .role(RoleDTO.USER)
                .build();

        loginDTO = new LoginDTO("testUser", "testPassword");
    }

    @Test
    void shouldSaveUser_onRegister_givenUserInformation() throws Exception {
        mockMvc.perform( MockMvcRequestBuilders
                        .post(REGISTER_URL)
                        .content(new ObjectMapper().writeValueAsString(registerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetToken_onLogin_givenLoginDTO() throws Exception {
        UserPrincipal userPrincipal = new UserPrincipal(
                loginDTO.getUsername(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                new Role("USER")
        );
        TokenDTO tokenDTO = new TokenDTO(jwtService.generateToken(userPrincipal));

        when(customerService.login(loginDTO)).thenReturn(new ResponseModel<>(true,tokenDTO));

        mockMvc.perform( MockMvcRequestBuilders
                        .post(LOGIN_URL)
                        .content(new ObjectMapper().writeValueAsString(loginDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorized_onLogin_givenInvalidUsername() throws Exception {
        when(customerService.login(any()))
                .thenThrow(new FieldValidationException("username", "not found"));

        mockMvc.perform( MockMvcRequestBuilders
                        .post(LOGIN_URL)
                        .content(new ObjectMapper().writeValueAsString(loginDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
