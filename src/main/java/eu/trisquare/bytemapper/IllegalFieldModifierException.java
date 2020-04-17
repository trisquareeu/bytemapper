package eu.trisquare.bytemapper;

import java.lang.reflect.Field;

/**
 * Used when mapped field is declared either static or final
 */
public class IllegalFieldModifierException extends IllegalArgumentException {

    IllegalFieldModifierException(Field field) {
        super(getMessage(field));
    }


    private static String getMessage(Field field) {
        return "Unable to set value for field: " +
                field.getName() + ". Mapped field must not be static nor final.";
    }

}
