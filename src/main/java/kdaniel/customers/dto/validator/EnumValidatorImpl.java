package kdaniel.customers.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kdaniel.customers.model.Role;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    private Role[] enumValues;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        // Get the enum constants dynamically, no casting needed
        this.enumValues = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null érték esetén validnak tekintjük a mezőt, ha nem kötelező
        }

        for (Role enumValue : enumValues) {
            if (enumValue.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
