package eu.trisquare.bytemapper;

/**
 * Used when data size is lower than 1 byte.
 */
public class InvalidSizeException extends IllegalArgumentException {

    InvalidSizeException(int size) {
        super(String.format(
                "Size should be bigger than 0, but %d was provided",
                size
        ));
    }

}
