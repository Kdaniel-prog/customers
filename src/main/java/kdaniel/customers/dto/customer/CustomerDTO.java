package kdaniel.customers.dto.customer;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String fullName;
    private Integer age;
    private String email;
}
