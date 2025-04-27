package kdaniel.customers.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Egy egységes válasz objektum sikeres és hibás válaszokra is.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel<T> {
    private boolean success;
    private T data;
}