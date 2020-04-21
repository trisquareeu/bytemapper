package eu.trisquare.bytemapper.fieldmapper;

import org.apache.commons.lang3.ClassUtils;

import java.nio.ByteBuffer;


/**
 * Default mapper for all single-value data types, i.e. numbers
 */
class SingleValueFieldMapper implements FieldMapper {

    /**
     * Holds maximum allowed size of processed data type, i.e. 8 bytes for long
     */
    protected final int maximumSupportedSize;

    /**
     * Holds returned data type.
     */
    private final Class<?> returnedType;
    /**
     * Mapper implementation
     */
    private final ByteBufferMapper endiannessAwareMapper;

    /**
     * Creates SingleValueFieldMapper for given arguments
     */
    SingleValueFieldMapper(
            ByteBufferMapper endiannessAwareMapper,
            int maxSupportedSize,
            Class<?> returnedType
    ) {
        this.maximumSupportedSize = maxSupportedSize;
        this.returnedType = returnedType;
        this.endiannessAwareMapper = endiannessAwareMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEligible(Class<?> type) {
        return ClassUtils.isAssignable(returnedType, type);
    }

    /**
     * {@inheritDoc}
     *
     * @throws TooSmallDatatypeException when data type cannot fit requested amount of bytes
     */
    @Override
    public Object getValue(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        checkSize(size);
        return this.map(buffer, isBigEndian, startByte, size);
    }

    /**
     * Checks if given amount of bytes is valid for mapped data type
     *
     * @param requestedSize is amount of bytes to map into value
     * @throws TooSmallDatatypeException when data type cannot fit requested amount of bytes
     */
    protected void checkSize(int requestedSize) {
        if (requestedSize > maximumSupportedSize) {
            throw new TooSmallDatatypeException(returnedType, maximumSupportedSize, requestedSize);
        }
    }

    /**
     * Converts selected part of ByteBuffer into specific Object instance
     *
     * @param buffer      used as a data source
     * @param isBigEndian which is true for big-endian values and false otherwise
     * @param startByte   is zero-inclusive index of value's first byte
     * @param size        determines index of value's last byte
     * @return mapped value
     */
    public Object map(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return endiannessAwareMapper.map(buffer, isBigEndian, startByte, size);
    }

    /**
     * Interface for mappers performing conversion from provided ByteBuffer's data range to specific Object instance
     */
    @FunctionalInterface
    interface ByteBufferMapper {
        /**
         * Converts selected ByteBuffer's data range to actual Object instance
         */
        Object map(ByteBuffer chunk, boolean isBigEndian, int startByte, int size);
    }

}
