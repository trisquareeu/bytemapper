package eu.trisquare.bytemapper.fieldmapper;

import java.nio.ByteBuffer;

/**
 * Interface for mappers obtaining values from ByteBuffer input to actual Object instances
 */
public interface FieldMapper {

    /**
     * Checks if given {@link eu.trisquare.bytemapper.fieldmapper.FieldMapper} supports
     * given data type.
     *
     * @param type to check if is assignable from mapper returned value
     * @return true if type is assignable from returned value type.
     */
    boolean isEligible(Class<?> type);

    /**
     * Performs conversion of given n- bytes, starting from provided index into
     * actual Object instance. Object sub-type depends of mapper implementation.
     *
     * @param buffer      used as a data source
     * @param isBigEndian which is true for big-endian values and false for little-endian
     * @param startByte   is zero-inclusive index of first byte of mapped value
     * @param size        determines last byte of parsed value
     * @return mapped value
     */
    Object getValue(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

}
