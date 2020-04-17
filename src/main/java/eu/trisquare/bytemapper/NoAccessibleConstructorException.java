package eu.trisquare.bytemapper;

/**
 * Used when instantiated class has no public default constructor or is non-static inner class.
 */
public class NoAccessibleConstructorException extends RuntimeException {

    NoAccessibleConstructorException(Class<?> clazz) {
        super("Provided class must have public default constructor and must not be non-static nested class: " + clazz.getSimpleName());
    }

}
