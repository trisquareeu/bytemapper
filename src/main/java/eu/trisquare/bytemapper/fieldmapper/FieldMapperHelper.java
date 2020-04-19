package eu.trisquare.bytemapper.fieldmapper;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Helper class for ByteBuffer to specific Object coversion
 */
public class FieldMapperHelper {

    private FieldMapperHelper() {
        //empty
    }

    /**
     * Returns selected byte of given ByteBuffer
     */
    static byte toByte(ByteBuffer buffer, @SuppressWarnings("unused") boolean isBigEndian, int startByte, @SuppressWarnings("unused") int size) {
        return buffer.get(startByte);
    }

    /**
     * Converts selected ByteBuffer content range to boolean value, adhering to
     * C standard, where all-zeroes value maps to false, otherwise is considered
     * as true. There is no limit in supported size.
     */
    static boolean toBoolean(ByteBuffer buffer, @SuppressWarnings("unused") boolean isBigEndian, int startByte, int size) {
        boolean outcome = false;
        for (int i = startByte; i < startByte + size; i++) {
            outcome |= buffer.get(i) != 0x00;
        }
        return outcome;
    }

    /**
     * Converts selected ByteBuffer content range to unsigned short value.
     *
     * @throws ArithmeticException if the value of mapped value will not exactly fit in a {@code short}.
     */
    static short toShort(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final BigInteger value = toBigInteger(buffer, isBigEndian, startByte, size);
        return value.shortValueExact();
    }

    /**
     * Converts selected ByteBuffer content range to unsigned integer value.
     *
     * @throws ArithmeticException if the value of mapped value will not exactly fit in a {@code int}.
     */
    static int toInt(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final BigInteger value = toBigInteger(buffer, isBigEndian, startByte, size);
        return value.intValueExact();
    }

    /**
     * Converts selected ByteBuffer content range to unsigned long value.
     *
     * @throws ArithmeticException if the value of mapped value will not exactly fit in a {@code long}.
     */
    static long toLong(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        final BigInteger value = toBigInteger(buffer, isBigEndian, startByte, size);
        return value.longValueExact();
    }

    /**
     * Converts selected ByteBuffer content range to unsigned BigInteger value.
     */
    static BigInteger toBigInteger(ByteBuffer buffer, boolean isBigEndian, int startByte, int size) {
        BigInteger value = BigInteger.ZERO;
        for (int i = 0; i < size; i++) {
            final int current = buffer.get(startByte + i) & 0xFF;
            final int bitShift = isBigEndian ? ((size - i - 1) * 8) : (i * 8);
            value = BigInteger
                    .valueOf(current)
                    .shiftLeft(bitShift)
                    .add(value);
        }
        return value;
    }

    /**
     * Converts given primitive byte array into String using UTF-8 charset
     */
    static String toString(byte[] source) {
        return new String(source, StandardCharsets.UTF_8);
    }

    /**
     * Converts given primitive byte array into Byte objects array
     */
    static Byte[] toByteObjectArray(byte[] source) {
        final Byte[] destination = new Byte[source.length];
        for (int i = 0; i < source.length; i++) {
            destination[i] = source[i];
        }
        return destination;
    }

}
