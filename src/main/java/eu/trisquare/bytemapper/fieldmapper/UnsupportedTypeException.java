package eu.trisquare.bytemapper.fieldmapper;

/**
 * Used when no eligible mapper was found for given data type
 */
public class UnsupportedTypeException extends IllegalArgumentException {

    UnsupportedTypeException(Class<?> clazz) {
        super("No mapper has been found for class: " + clazz.getName());
    }

}
