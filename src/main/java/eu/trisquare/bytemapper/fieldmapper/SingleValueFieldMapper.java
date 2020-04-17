package eu.trisquare.bytemapper.fieldmapper;

import java.nio.ByteBuffer;


/**
 * Default mapper for all single-value data types, i.e. numbers
 */
public class SingleValueFieldMapper extends FieldMapperBase {

    /**
     * Mapper implementation
     */
    private final EndiannessAwareByteBufferMapper endiannessAwareMapper;

    /**
     * Creates SingleValueFieldMapper for given arguments
     */
    SingleValueFieldMapper(EndiannessAwareByteBufferMapper endiannessAwareMapper, int maxSupportedSize, Class<?> supportedType) {
        super(maxSupportedSize, supportedType);
        this.endiannessAwareMapper = endiannessAwareMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object map(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return endiannessAwareMapper.map(buffer, isBigEndian, startByte, size);
    }

    /**
     * Interface for mappers performing conversion from provided ByteBuffer's data range to specific Object instance
     */
    @FunctionalInterface
    interface EndiannessAwareByteBufferMapper {
        /**
         * Converts selected ByteBuffer's data range to actual Object instance
         */
        Object map(ByteBuffer chunk, boolean isBigEndian, int startByte, int size);
    }

}
