package eu.trisquare.bytemapper.fieldmapper;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardTypeMapperTest {

    private final TypeMapper mapper = new StandardTypeMapper();

    private static Stream<Long> longValuesProvider() {
        return Stream.of(
                Long.MIN_VALUE,
                Long.MIN_VALUE + 1,
                0L,
                Long.MAX_VALUE - 1,
                Long.MAX_VALUE
        );
    }

    private static Stream<Integer> intValuesProvider() {
        return Stream.of(
                Integer.MIN_VALUE,
                Integer.MIN_VALUE + 1,
                0,
                Integer.MAX_VALUE - 1,
                Integer.MAX_VALUE
        );
    }

    private static Stream<Short> shortValuesProvider() {
        return Stream.of(
                Short.MIN_VALUE,
                (short) (Short.MIN_VALUE + 1),
                (short) 0,
                (short) (Short.MAX_VALUE - 1),
                Short.MAX_VALUE
        );
    }

    private static Stream<Byte> byteValuesProvider() {
        return Stream.of(
                Byte.MIN_VALUE,
                (byte) (Byte.MIN_VALUE + 1),
                (byte) 0,
                (byte) (Byte.MAX_VALUE - 1),
                Byte.MAX_VALUE
        );
    }

    private static Stream<Float> floatValuesProvider() {
        return Stream.of(
                Float.MIN_VALUE,
                -1F,
                -0.12345F,
                0f,
                0.12345F,
                1F,
                Float.MAX_VALUE
        );
    }

    private static Stream<Double> doubleValuesProvider() {
        return Stream.of(
                Double.MIN_VALUE,
                -1d,
                -0.12345d,
                0d,
                0.12345d,
                1d,
                Double.MAX_VALUE
        );
    }

    @MethodSource("byteValuesProvider")
    @ParameterizedTest
    void toBooleanShouldReturnFalseOnlyIfGivenZeroes(byte b) {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(b);
        buffer.flip();

        assertEquals(b != 0, mapper.toBoolean(buffer, true, 0, 1));
        assertEquals(b != 0, mapper.toBoolean(buffer, false, 0, 1));

    }

    @MethodSource("byteValuesProvider")
    @ParameterizedTest
    void toByteShouldReturnCorrectValue(byte b) {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(b);
        buffer.flip();

        assertEquals(b, mapper.toByte(buffer, true, 0, 1));
        assertEquals(b, mapper.toByte(buffer, false, 0, 1));
    }

    @MethodSource("shortValuesProvider")
    @ParameterizedTest
    void toShortShouldReturnCorrectValue(short s) {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(s);
        buffer.flip();

        assertEquals(s, mapper.toShort(buffer, true, 0, 2));
        assertEquals(Short.reverseBytes(s), mapper.toShort(buffer, false, 0, 2));
    }

    @MethodSource("intValuesProvider")
    @ParameterizedTest
    void toIntShouldReturnCorrectValue(int i) {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(i);
        buffer.flip();

        assertEquals(i, mapper.toInt(buffer, true, 0, 4));
        assertEquals(Integer.reverseBytes(i), mapper.toInt(buffer, false, 0, 4));
    }

    @MethodSource("longValuesProvider")
    @ParameterizedTest
    void toLongShouldReturnCorrectValue(long l) {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        buffer.flip();

        assertEquals(l, mapper.toLong(buffer, true, 0, 8));
        assertEquals(Long.reverseBytes(l), mapper.toLong(buffer, false, 0, 8));
    }

    @MethodSource("floatValuesProvider")
    @ParameterizedTest
    void toFloat(float f) {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(f);
        buffer.flip();

        assertEquals(f, mapper.toFloat(buffer, true, 0, 4));
        assertEquals(
                Float.intBitsToFloat(Integer.reverseBytes(Float.floatToRawIntBits(f))),
                mapper.toFloat(buffer, false, 0, 4)
        );
    }

    @MethodSource("doubleValuesProvider")
    @ParameterizedTest
    void toDouble(double d) {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putDouble(d);
        buffer.flip();

        assertEquals(d, mapper.toDouble(buffer, true, 0, 8));
        assertEquals(
                Double.longBitsToDouble(Long.reverseBytes(Double.doubleToRawLongBits(d))),
                mapper.toDouble(buffer, false, 0, 8)
        );
    }

    @Test
    void toBigInteger() {
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(Long.MIN_VALUE);
        buffer.putLong(Long.MAX_VALUE);
        buffer.flip();

        final BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE).add(
                BigInteger.valueOf(Long.MIN_VALUE).shiftLeft(64)
        );

        final BigInteger rBi = BigInteger.valueOf(Long.reverseBytes(Long.MIN_VALUE)).add(
                BigInteger.valueOf(Long.reverseBytes(Long.MAX_VALUE)).shiftLeft(64)
        );

        assertEquals(bi, mapper.toBigInteger(buffer, true, 0, 16));
        assertEquals(rBi, mapper.toBigInteger(buffer, false, 0, 16));
    }

    @Test
    void testToString() {
        final String testString = "123ABC,.'{}";
        final String reversedString = new StringBuilder(testString).reverse().toString();
        ByteBuffer buffer = ByteBuffer.wrap(testString.getBytes());
        assertEquals(testString, mapper.toString(buffer, true, 0, testString.length()));
        assertEquals(reversedString, mapper.toString(buffer, false, 0, testString.length()));
    }

    @Test
    void toByteArray() {
        final byte[] bytes = new byte[]{
                Byte.MIN_VALUE, 0, 1, 2, 3, 4, 5, Byte.MAX_VALUE
        };

        final byte[] reversed = ArrayUtils.clone(bytes);
        ArrayUtils.reverse(reversed);

        final ByteBuffer buffer = ByteBuffer.wrap(bytes);

        assertArrayEquals(bytes, mapper.toByteArray(buffer, true, 0, bytes.length));
        assertArrayEquals(reversed, mapper.toByteArray(buffer, false, 0, bytes.length));
    }

    @Test
    void toByteObjectArray() {
        final Byte[] bytes = new Byte[]{
                Byte.MIN_VALUE, 0, 1, 2, 3, 4, 5, Byte.MAX_VALUE
        };

        final Byte[] reversed = ArrayUtils.clone(bytes);
        ArrayUtils.reverse(reversed);

        final ByteBuffer buffer = ByteBuffer.wrap(ArrayUtils.toPrimitive(bytes));

        assertArrayEquals(bytes, mapper.toByteObjectArray(buffer, true, 0, bytes.length));
        assertArrayEquals(reversed, mapper.toByteObjectArray(buffer, false, 0, bytes.length));
    }
}