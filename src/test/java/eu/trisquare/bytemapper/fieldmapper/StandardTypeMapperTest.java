package eu.trisquare.bytemapper.fieldmapper;

import eu.trisquare.bytemapper.annotations.ByteMapperConstructor;
import eu.trisquare.bytemapper.annotations.Value;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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


    @Test
    void toAnnotatedFieldsStructure() {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put((byte) 0);
        buffer.put(Byte.MAX_VALUE);
        buffer.putShort(Short.MAX_VALUE);
        buffer.putInt(Integer.MAX_VALUE);
        buffer.flip();
        final AnnotatedFieldsStructureClass result = mapper.toStructure(buffer, AnnotatedFieldsStructureClass.class, 0, 8);
        assertFalse(result.booleanValue);
        assertEquals(Byte.MAX_VALUE, result.byteValue);
        assertEquals(Short.MAX_VALUE, result.shortValue);
        assertEquals(Integer.MAX_VALUE, result.intValue);
    }

    @Test
    void toAnnotatedConstructorStructure() {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put((byte) 0);
        buffer.put(Byte.MAX_VALUE);
        buffer.putShort(Short.MAX_VALUE);
        buffer.putInt(Integer.MAX_VALUE);
        buffer.flip();
        final AnnotatedConstructorStructureClass result = mapper.toStructure(buffer, AnnotatedConstructorStructureClass.class, 0, 8);
        assertFalse(result.booleanValue);
        assertEquals(Byte.MAX_VALUE, result.byteValue);
        assertEquals(Short.MAX_VALUE, result.shortValue);
        assertEquals(Integer.MAX_VALUE, result.intValue);
    }

    private static class AnnotatedConstructorStructureClass {

        private final boolean booleanValue;
        private final byte byteValue;
        private final short shortValue;
        private final int intValue;

        @ByteMapperConstructor
        public AnnotatedConstructorStructureClass(
                @Value(startByte = 0) boolean booleanValue,
                @Value(startByte = 1) byte byteValue,
                @Value(startByte = 2, size = 2) short shortValue,
                @Value(startByte = 4, size = 4) int intValue
        ) {
            this.booleanValue = booleanValue;
            this.byteValue = byteValue;
            this.shortValue = shortValue;
            this.intValue = intValue;
        }
    }

    @SuppressWarnings("unused")
    private static class AnnotatedFieldsStructureClass {

        @Value(startByte = 0)
        private boolean booleanValue;

        @Value(startByte = 1)
        private byte byteValue;

        @Value(startByte = 2, size = 2)
        private short shortValue;

        @Value(startByte = 4, size = 4)
        private int intValue;

        public AnnotatedFieldsStructureClass() {
            //empty
        }

    }


}