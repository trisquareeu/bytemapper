package eu.trisquare.bytemapper.fieldmapper;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;

/**
 * Default implementation of FieldMapper to process multi-value fields,
 * like arrays and Strings, allowing them to be created from primitive
 * byte arrays.
 */
public class ArrayFieldMapper extends FieldMapperBase {

    /**
     * Implementation of mapper supporting given data type
     */
    private final ByteArrayMapper mapper;

    /**
     * Creates ArrayFieldMapper.
     *
     * @param mapper        that supports data type creation from primitive byte array input
     * @param supportedType is used to determine if given mapper is eligible to use for annotated data type
     */
    ArrayFieldMapper(ByteArrayMapper mapper, Class<?> supportedType) {
        super(0, supportedType);
        this.mapper = mapper;
    }

    /**
     * Obtains primitive byte array from given ByteBuffer
     */
    private static byte[] readBytes(ByteBuffer buffer, int startByte, int size) {
        final ByteBuffer src = (ByteBuffer) buffer.duplicate().position(startByte);
        final byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = src.get();
        }
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object map(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final byte[] bytes = readBytes(buffer, startByte, size);
        if (!isBigEndian) {
            ArrayUtils.reverse(bytes);
        }
        return mapper.map(bytes);
    }

    /**
     * As byte array does not have upper size-limit, this method permits all sizes to be processed
     */
    @Override
    protected void checkSize(int requestedSize) {
        //do nothing
    }

    /**
     * Interface for mappers performing conversion from primitive byte array to specific Object instance
     */
    @FunctionalInterface
    interface ByteArrayMapper {
        /**
         * Converts primitive byte array to actual Object instance
         */
        Object map(byte[] bytes);
    }
}
