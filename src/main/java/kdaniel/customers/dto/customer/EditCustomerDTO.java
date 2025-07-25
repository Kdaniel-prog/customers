package kdaniel.customers.dto.customer;

import jakarta.validation.constraints.NotNull;
import kdaniel.customers.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditCustomerDTO {
    @NotNull(message = "Id must not be empty")
    private Long id;
    private String username;
    private String fullName;
    private String password;
    private String email;
    private byte age;
    private Role role;
}
