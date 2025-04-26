package kdaniel.customers.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Component
public class FieldValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public FieldValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

}
