package kdaniel.customers.dto.customer;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerDTO {
    private String fullName;
    private byte age;
    private String email;
}
