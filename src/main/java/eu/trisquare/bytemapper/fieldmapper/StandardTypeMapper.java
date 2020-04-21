package eu.trisquare.bytemapper.fieldmapper;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Helper class for ByteBuffer to specific Object coversion
 */
class StandardTypeMapper implements TypeMapper {

    StandardTypeMapper() {
        //empty
    }


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


    @Override
    public byte toByte(
            ByteBuffer buffer,
            boolean isBigEndian,
            int startByte,
            int size
    ) {
        return getSlice(Byte.BYTES, buffer, isBigEndian, startByte, size).get();
    }


    @Override
    public short toShort(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Short.BYTES, buffer, isBigEndian, startByte, size).getShort();
    }


    @Override
    public int toInt(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Integer.BYTES, buffer, isBigEndian, startByte, size).getInt();
    }


    @Override
    public long toLong(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Long.BYTES, buffer, isBigEndian, startByte, size).getLong();
    }

    @Override
    public double toDouble(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Double.BYTES, buffer, isBigEndian, startByte, size).getDouble();
    }

    @Override
    public float toFloat(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return getSlice(Float.BYTES, buffer, isBigEndian, startByte, size).getFloat();
    }

    @Override
    public BigInteger toBigInteger(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final byte[] bytes = readBytes(buffer, isBigEndian, startByte, size);
        return new BigInteger(bytes);
    }

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

    @Override
    public String toString(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final byte[] bytes = readBytes(buffer, isBigEndian, startByte, size);
        return new String(bytes, StandardCharsets.UTF_8);
    }


    @Override
    public byte[] toByteArray(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        return readBytes(buffer, isBigEndian, startByte, size);
    }


    @Override
    public Byte[] toByteObjectArray(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final byte[] bytes = readBytes(buffer, isBigEndian, startByte, size);
        return ArrayUtils.toObject(bytes);
    }

    /**
     * Obtains primitive byte array from given ByteBuffer
     */
    private byte[] readBytes(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final byte[] bytes = new byte[size];
        for (int n = 0; n < size; n++) {
            final int byteIdx = isBigEndian ? n : (size - n - 1);
            bytes[n] = buffer.get(startByte + byteIdx);
        }
        return bytes;
    }

}
