package kdaniel.customers.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FieldValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public FieldValidationException(Map<String, String> errors) {
        this.errors = errors;
    }

    public FieldValidationException(String field, String error) {
        errors =  new HashMap<>();
        errors.put(field, error);
    }

}
