package kdaniel.customers.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JWTResponseDTO {
    private String token;
}
