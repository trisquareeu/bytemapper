package eu.trisquare.bytemapper.fieldmapper;

import org.apache.commons.lang3.ClassUtils;

import java.nio.ByteBuffer;

/**
 * Abstract class supporting common processing of all mapper implementations
 */
abstract class FieldMapperBase implements FieldMapper {

    /**
     * Holds maximum allowed size of processed data type, i.e. 8 bytes for long
     */
    protected final int maximumSupportedSize;

    /**
     * Holds returned data type.
     */
    private final Class<?> returnedType;

    /**
     * Provides values for abstract class fields and methods
     */
    protected FieldMapperBase(int maxSupportedSize, Class<?> returnedType) {
        this.maximumSupportedSize = maxSupportedSize;
        this.returnedType = returnedType;
    }

    /**
     * Converts selected part of ByteBuffer into specific Object instance
     * @param buffer used as a data source
     * @param isBigEndian which is true for big-endian values and false otherwise
     * @param startByte is zero-inclusive index of value's first byte
     * @param size determines index of value's last byte
     * @return mapped value
     */
    protected abstract Object map(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEligible(Class<?> type) {
        return ClassUtils.isAssignable(returnedType, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        checkSize(size);
        return this.map(buffer, isBigEndian, startByte, size);
    }

    /**
     * Checks if given amount of bytes is valid for mapped data type
     * @param requestedSize is amount of bytes to map into value
     */
    protected void checkSize(int requestedSize) {
        if (requestedSize > maximumSupportedSize) {
            throw new TooSmallDatatypeException(returnedType, maximumSupportedSize, requestedSize);
        }
    }


}