package eu.trisquare.bytemapper.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Fields decorated with this annotation will be processed by {@link eu.trisquare.bytemapper.ByteMapper} as a composite
 * structures. Annotated fields must not be final nor static.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, PARAMETER})
public @interface Structure {

    /**
     * Inclusive index of first byte of value, starting from 0.
     * This value must be lower than input data length.
     *
     * @return index of value's first byte
     */
    int startByte();

    /**
     * Number of bytes used to obtain structure starting from startByte.
     * <p>
     * This value must not be lower than index of last byte mapped by
     * structure.
     *
     * @return length of structure in bytes
     */
    int size();

}
