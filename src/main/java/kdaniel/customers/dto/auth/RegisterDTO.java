package kdaniel.customers.dto.auth;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDTO {

    @Size(max = 255, message = "username is too long")
    @NotBlank(message = "Username must not be empty")
    private String username;

    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    @NotBlank(message = "Password must not be empty")
    private String password;

    @Size(max = 255, message = "fullName is too long")
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

    @NotNull(message = "Role must not be empty")
    private RoleDTO role;
}
