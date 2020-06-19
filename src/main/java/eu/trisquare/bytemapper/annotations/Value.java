package eu.trisquare.bytemapper.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Fields decorated with this annotation will be processed by {@link eu.trisquare.bytemapper.ByteMapper} as a simple
 * values. Annotated fields must not be final nor static.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, PARAMETER})
public @interface Value {

    /**
     * Inclusive index of first byte of value, starting from 0.
     * This value must be lower than input data length.
     *
     * @return index of value's first byte
     */
    int startByte();

    /**
     * Number of bytes used to obtain value starting from startByte.
     * <p>
     * This value must not exceed size of data type annotated by this field,
     * (i.e. size for annotated int must be lower or equal to 4) and sum of
     * startByte and size must not exceed input data length.
     *
     * @return length of value in bytes
     */
    int size() default Byte.BYTES;


    /**
     * Sets endianness of field. By default, values are treated as big endian, where most significant
     * byte of multibyte value comes first, and least significant byte comes last.
     * <p>
     * By setting this value to false, you achieve similar effect as by reversing byte order of input
     * in range limited by startByte and startByte+size values.
     *
     * @return true if value is big-endian
     */
    boolean bigEndian() default true;

}
