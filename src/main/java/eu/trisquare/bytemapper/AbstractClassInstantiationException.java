package eu.trisquare.bytemapper;

/**
 * Used when class cannot be instantiated because it's either declared abstract or interface.
 */
public class AbstractClassInstantiationException extends RuntimeException {

    AbstractClassInstantiationException(Class<?> clazz) {
        super("Provided class must not be interface nor abstract class: " + clazz.getSimpleName());
    }

}
