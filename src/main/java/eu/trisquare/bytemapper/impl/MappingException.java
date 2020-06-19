package eu.trisquare.bytemapper.impl;

/**
 * Generic exception used for ByteMapper processing issues.
 */
public abstract class MappingException extends RuntimeException {

    protected MappingException(String message) {
        super(message);
    }

    protected MappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
