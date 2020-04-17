package eu.trisquare.bytemapper;

/**
 * Used when provided buffer's limit is lower than 1, i.e. buffer is empty
 */
public class EmptyBufferException extends IllegalArgumentException {

    EmptyBufferException(int bufferLimit) {
        super(String.format(
                "Buffer limit must be bigger than 0, but is %d.",
                bufferLimit
        ));
    }

}
