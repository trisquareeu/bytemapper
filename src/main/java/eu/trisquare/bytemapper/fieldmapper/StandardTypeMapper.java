package eu.trisquare.bytemapper.fieldmapper;

import eu.trisquare.bytemapper.impl.ByteMapperBuilder;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Helper class for ByteBuffer to specific Object conversion
 */
class StandardTypeMapper implements TypeMapper {
    /**
     * Creates StandardTypeMapper instance
     */
    StandardTypeMapper() {
        //empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean toBoolean(
            ByteBuffer buffer,
            @SuppressWarnings("unused") boolean isBigEndian,
            int startByte,
            int size
    ) {
        boolean outcome = false;
        for (int i = startByte; i < startByte + size; i++) {
            outcome |= buffer.get(i) != 0x00;
        }
        return outcome;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte toByte(
            ByteBuffer buffer,
            boolean isBigEndian,
            int startByte,
            int size
    ) {
        return getSlice(Byte.BYTES, buffer, isBigEndian, startByte, size).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short toShort(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Short.BYTES, buffer, isBigEndian, startByte, size).getShort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int toInt(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Integer.BYTES, buffer, isBigEndian, startByte, size).getInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long toLong(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Long.BYTES, buffer, isBigEndian, startByte, size).getLong();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double toDouble(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Double.BYTES, buffer, isBigEndian, startByte, size).getDouble();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float toFloat(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Float.BYTES, buffer, isBigEndian, startByte, size).getFloat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger toBigInteger(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final byte[] bytes = readBytes(buffer, isBigEndian, startByte, size);
        return new BigInteger(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final byte[] bytes = readBytes(buffer, isBigEndian, startByte, size);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] toByteArray(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return readBytes(buffer, isBigEndian, startByte, size);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Byte[] toByteObjectArray(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final byte[] bytes = readBytes(buffer, isBigEndian, startByte, size);
        return ArrayUtils.toObject(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T toStructure(ByteBuffer buffer, Class<T> structureType, int startByte, int size) {
        final ByteBuffer slice = getSlice(size, buffer, true, startByte, size);
        return new ByteMapperBuilder().build().mapValues(structureType, slice);
    }

    /**
     * Gets {@code size} bytes from {@code source}, starting from {@code startByte} and ending on
     * {@code startByte+size} as a primitive bytes array. If {@code isBigEndian} flag is set to
     * {@code false}, returned array will have opposite order than in source.
     *
     * @param source      to obtain data from
     * @param isBigEndian determines data traversal direction
     * @param startByte   position (0-inclusive index) of first byte to copy
     * @param size        number of bytes to copy
     * @return {@code size} bytes copied form {@code source} as a primitive bytes array in direct or opposite direction
     */
    private byte[] readBytes(ByteBuffer source, boolean isBigEndian, int startByte, int size) {
        final byte[] bytes = new byte[size];
        for (int n = 0; n < size; n++) {
            final int byteIdx = isBigEndian ? n : (size - n - 1);
            bytes[n] = source.get(startByte + byteIdx);
        }
        return bytes;
    }


    /**
     * Returns ByteBuffer with content length equal to {@code expectedSize}, containing {@code size} bytes slice
     * of input ByteBuffer source, starting from {@code startByte} and ending on {@code startByte+size}.
     * If {@code expectedSize} is bigger than {@code size}, zeroes will be used as a padding on MSB side.
     *
     * @param expectedSize expected size of slice, including optional padding
     * @param source       to copy data from
     * @param isBigEndian  determines data traversal direction
     * @param startByte    position (0-inclusive index) of first byte to copy
     * @param size         number of bytes to copy
     * @return slice of {@code expectedSize} bytes, containing {@code size} bytes copied form {@code source}
     */
    private ByteBuffer getSlice(int expectedSize, ByteBuffer source, boolean isBigEndian, int startByte, int size) {
        final ByteOrder order = isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        final ByteBuffer slice = ByteBuffer.allocate(expectedSize);
        slice.order(order);
        slice.put(new byte[expectedSize - size]);
        for (int n = 0; n < size; n++) {
            slice.put(source.get(startByte + n));
        }
        slice.flip();
        return slice;
    }

}
