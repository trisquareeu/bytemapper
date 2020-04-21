package eu.trisquare.bytemapper.fieldmapper;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class SingleValueFieldMapperTest {

    private final FieldMapperProvider mapperProvider = new FieldMapperProvider();

    @Test
    void mappingShouldWorkForPrimitiveBoolean() {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) 0x00FF);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(boolean.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(boolean.class));

        final boolean b1 = (boolean) mapper.getValue(buffer, true, 0, 2);
        assertTrue(b1);

        final boolean b2 = (boolean) mapper.getValue(buffer, false, 0, 2);
        assertTrue(b2);
    }

    @Test
    void mappingShouldWorkForObjectBoolean() {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) 0x00FF);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(Boolean.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(Boolean.class));
        final Boolean b1 = (Boolean) mapper.getValue(buffer, true, 0, 2);
        assertTrue(b1);

        final Boolean b2 = (Boolean) mapper.getValue(buffer, false, 0, 2);
        assertTrue(b2);
    }

    @Test
    void mappingShouldWorkForPrimitiveByte() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(Byte.MAX_VALUE);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(byte.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(byte.class));

        byte b1 = (byte) mapper.getValue(buffer, true, 0, 1);
        assertEquals(Byte.MAX_VALUE, b1);

        byte b2 = (byte) mapper.getValue(buffer, false, 0, 1);
        assertEquals(Byte.MAX_VALUE, b2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 2)
        );
        assertEquals(
                "For type byte maximum allowed size is 1, but requested parsing of 2 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }

    @Test
    void mappingShouldWorkForObjectByte() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(Byte.MAX_VALUE);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(Byte.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(Byte.class));

        Byte b1 = (Byte) mapper.getValue(buffer, true, 0, 1);
        assertEquals(Byte.MAX_VALUE, b1);

        Byte b2 = (Byte) mapper.getValue(buffer, false, 0, 1);
        assertEquals(Byte.MAX_VALUE, b2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 2)
        );
        assertEquals(
                "For type byte maximum allowed size is 1, but requested parsing of 2 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }

    @Test
    void mappingShouldWorkForPrimitiveShort() {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(Short.MAX_VALUE);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(short.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(short.class));

        short s1 = (short) mapper.getValue(buffer, true, 0, 2);
        assertEquals(Short.MAX_VALUE, s1);

        short s2 = (short) mapper.getValue(buffer, false, 0, 2);
        assertEquals(Short.reverseBytes(Short.MAX_VALUE), s2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 3)
        );
        assertEquals(
                "For type short maximum allowed size is 2, but requested parsing of 3 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }

    @Test
    void mappingShouldWorkForObjectShort() {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(Short.MAX_VALUE);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(Short.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(Short.class));

        Short s1 = (Short) mapper.getValue(buffer, true, 0, 2);
        assertEquals(Short.MAX_VALUE, s1);

        Short s2 = (Short) mapper.getValue(buffer, false, 0, 2);
        assertEquals(Short.reverseBytes(Short.MAX_VALUE), s2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 3)
        );
        assertEquals(
                "For type short maximum allowed size is 2, but requested parsing of 3 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }


    @Test
    void mappingShouldWorkForPrimitiveInt() {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(Integer.MAX_VALUE);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(int.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(int.class));

        int i1 = (int) mapper.getValue(buffer, true, 0, 4);
        assertEquals(Integer.MAX_VALUE, i1);

        int i2 = (int) mapper.getValue(buffer, false, 0, 4);
        assertEquals(Integer.reverseBytes(Integer.MAX_VALUE), i2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 5)
        );
        assertEquals(
                "For type int maximum allowed size is 4, but requested parsing of 5 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }

    @Test
    void mappingShouldWorkForObjectInt() {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(Integer.MAX_VALUE);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(Integer.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(Integer.class));

        Integer i1 = (Integer) mapper.getValue(buffer, true, 0, 4);
        assertEquals(Integer.MAX_VALUE, i1);

        Integer i2 = (Integer) mapper.getValue(buffer, false, 0, 4);
        assertEquals(Integer.reverseBytes(Integer.MAX_VALUE), i2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 5)
        );
        assertEquals(
                "For type int maximum allowed size is 4, but requested parsing of 5 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }


    @Test
    void mappingShouldWorkForPrimitiveLong() {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(Long.MAX_VALUE);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(long.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(long.class));

        long i1 = (long) mapper.getValue(buffer, true, 0, 8);
        assertEquals(Long.MAX_VALUE, i1);

        long i2 = (long) mapper.getValue(buffer, false, 0, 8);
        assertEquals(Long.reverseBytes(Long.MAX_VALUE), i2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 9)
        );
        assertEquals(
                "For type long maximum allowed size is 8, but requested parsing of 9 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }

    @Test
    void mappingShouldWorkForObjectLong() {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(Long.MAX_VALUE);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(Long.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(Long.class));

        Long i1 = (Long) mapper.getValue(buffer, true, 0, 8);
        assertEquals(Long.MAX_VALUE, i1);

        Long i2 = (Long) mapper.getValue(buffer, false, 0, 8);
        assertEquals(Long.reverseBytes(Long.MAX_VALUE), i2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 9)
        );
        assertEquals(
                "For type long maximum allowed size is 8, but requested parsing of 9 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }


    @Test
    void mappingShouldWorkForPrimitiveFloat() {
        final float f = 12345.6789F;
        final float reversed = Float.intBitsToFloat(
                Integer.reverseBytes(Float.floatToRawIntBits(f))
        );

        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(12345.6789F);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(float.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(float.class));

        float f1 = (float) mapper.getValue(buffer, true, 0, 4);
        assertEquals(f, f1);

        float f2 = (float) mapper.getValue(buffer, false, 0, 4);
        assertEquals(reversed, f2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 5)
        );
        assertEquals(
                "For type float maximum allowed size is 4, but requested parsing of 5 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }

    @Test
    void mappingShouldWorkForObjectFloat() {
        final float f = 12345.6789F;
        final float reversed = Float.intBitsToFloat(
                Integer.reverseBytes(Float.floatToRawIntBits(f))
        );

        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(f);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(Float.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(Float.class));

        Float f1 = (Float) mapper.getValue(buffer, true, 0, 4);
        assertEquals(f, f1);

        Float f2 = (Float) mapper.getValue(buffer, false, 0, 4);
        assertEquals(reversed, f2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 5)
        );
        assertEquals(
                "For type float maximum allowed size is 4, but requested parsing of 5 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }

    @Test
    void mappingShouldWorkForPrimitiveDouble() {
        final double d = 12345.6789D;
        final double reversed = Double.longBitsToDouble(
                Long.reverseBytes(Double.doubleToRawLongBits(d))
        );

        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putDouble(d);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(double.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(double.class));

        double d1 = (double) mapper.getValue(buffer, true, 0, 8);
        assertEquals(d, d1);

        double d2 = (double) mapper.getValue(buffer, false, 0, 8);
        assertEquals(reversed, d2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 9)
        );
        assertEquals(
                "For type double maximum allowed size is 8, but requested parsing of 9 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }

    @Test
    void mappingShouldWorkForObjectDouble() {
        final double d = 12345.6789D;
        final double reversed = Double.longBitsToDouble(
                Long.reverseBytes(Double.doubleToRawLongBits(d))
        );

        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putDouble(d);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(Double.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(Double.class));

        Double d1 = (Double) mapper.getValue(buffer, true, 0, 8);
        assertEquals(d, d1);

        Double d2 = (Double) mapper.getValue(buffer, false, 0, 8);
        assertEquals(reversed, d2);

        final Exception e = assertThrows(
                TooSmallDatatypeException.class,
                () -> mapper.getValue(buffer, true, 0, 9)
        );
        assertEquals(
                "For type double maximum allowed size is 8, but requested parsing of 9 bytes. " +
                        "Would you like to use different data type?",
                e.getMessage()
        );
    }

    @Test
    void mappingShouldWorkForPrimitiveByteArray() {
        final byte[] array = new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE};
        final byte[] reversed = ArrayUtils.clone(array);
        ArrayUtils.reverse(reversed);
        final ByteBuffer buffer = ByteBuffer.wrap(array);

        final FieldMapper mapper = mapperProvider.getMapper(byte[].class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(byte[].class));

        byte[] a1 = (byte[]) mapper.getValue(buffer, true, 0, 3);
        assertArrayEquals(array, a1);

        byte[] a2 = (byte[]) mapper.getValue(buffer, false, 0, 3);
        assertArrayEquals(reversed, a2);
    }

    @Test
    void mappingShouldWorkForObjectByteArray() {
        final byte[] array = new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE};
        final byte[] reversed = ArrayUtils.clone(array);
        ArrayUtils.reverse(reversed);
        final ByteBuffer buffer = ByteBuffer.wrap(array);

        final FieldMapper mapper = mapperProvider.getMapper(Byte[].class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(Byte[].class));

        Byte[] a1 = (Byte[]) mapper.getValue(buffer, true, 0, 3);
        assertArrayEquals(ArrayUtils.toObject(array), a1);

        Byte[] a2 = (Byte[]) mapper.getValue(buffer, false, 0, 3);
        assertArrayEquals(ArrayUtils.toObject(reversed), a2);
    }


    @Test
    void mappingShouldWorkForBigInteger() {
        final BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE).add(
                BigInteger.valueOf(Long.MIN_VALUE).shiftLeft(64)
        );

        final BigInteger rBi = BigInteger.valueOf(Long.reverseBytes(Long.MIN_VALUE)).add(
                BigInteger.valueOf(Long.reverseBytes(Long.MAX_VALUE)).shiftLeft(64)
        );

        final ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(Long.MIN_VALUE);
        buffer.putLong(Long.MAX_VALUE);
        buffer.flip();

        final FieldMapper mapper = mapperProvider.getMapper(BigInteger.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(BigInteger.class));

        assertEquals(bi, mapper.getValue(buffer, true, 0, 16));
        assertEquals(rBi, mapper.getValue(buffer, false, 0, 16));
    }

    @Test
    void mappingShouldWorkForBigString() {
        final String testString = "123ABC,.'{}";
        final String reversedString = new StringBuilder(testString).reverse().toString();
        ByteBuffer buffer = ByteBuffer.wrap(testString.getBytes());

        final FieldMapper mapper = mapperProvider.getMapper(String.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(String.class));

        assertEquals(testString, mapper.getValue(buffer, true, 0, testString.length()));
        assertEquals(reversedString, mapper.getValue(buffer, false, 0, testString.length()));
    }

    @Test
    void signedByteValueShouldBeUnsignedForBiggerTypes(){
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte)0xFF);
        buffer.flip();

        final FieldMapper byteMapper = mapperProvider.getMapper(byte.class);
        byte b = (byte) byteMapper.getValue(buffer, true, 0, 1);
        assertEquals(-1, b);

        final FieldMapper shortMapper = mapperProvider.getMapper(short.class);
        short s = (short) shortMapper.getValue(buffer, true, 0, 1);
        assertEquals(255, s);

        final FieldMapper intMapper = mapperProvider.getMapper(int.class);
        int i = (int) intMapper.getValue(buffer, true, 0, 1);
        assertEquals(255, i);

        final FieldMapper longMapper = mapperProvider.getMapper(long.class);
        long l = (long) longMapper.getValue(buffer, true, 0, 1);
        assertEquals(255, l);
    }


    @Test
    void signedBigIntegerCanBeConvertedToUnsigned(){
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte)0xFF);
        buffer.flip();

        final FieldMapper bigIntegerMapper = mapperProvider.getMapper(BigInteger.class);
        BigInteger signed = (BigInteger) bigIntegerMapper.getValue(buffer, true, 0, 1);
        assertEquals(BigInteger.valueOf(-1), signed);

        final BigInteger unsigned = new BigInteger(1, signed.toByteArray());
        assertEquals(BigInteger.valueOf(255), unsigned);
    }
}