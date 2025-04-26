package kdaniel.customers.dto.auth;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {

    @NotBlank(message = "Username must not be empty")
    private String username;

    @Min(value = 6, message = "Password must be 6 characters")
    @NotBlank(message = "Password must not be empty")
    private String password;

    @NotBlank(message = "Full name must not be empty")
    private String fullName;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Email confirmation must not be empty")
    @Email(message = "Invalid email format")
    private String confirmEmail;

    @Min(value = 0, message = "Age must be a positive number")
    @Max(value = 127, message = "Age is too high")
    private byte age;

    @NotBlank(message = "Role must not be empty")
    @Pattern(regexp = "ADMIN|USER", message = "Role must be either 'ADMIN' or 'USER'")
    private String role;
}
