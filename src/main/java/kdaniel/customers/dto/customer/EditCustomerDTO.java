package kdaniel.customers.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditCustomerDTO {
    @NotBlank(message = "Id must not be empty")
    private Long id;
    private String username;
    private String fullName;
    private String password;
    private String email;
    private byte age;
}
