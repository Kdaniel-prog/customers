package kdaniel.customers.dto.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO {
    private String fullName;
    private byte age;
    private String email;

    public CustomerDTO(String fullName, byte age, String email) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }
}
