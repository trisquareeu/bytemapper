package eu.trisquare.bytemapper.fieldmapper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class FieldMapperProviderTest {

    private static Stream<Class<?>> booleanClassesProvider() {
        return Stream.of(Boolean.class, boolean.class);
    }

    private static Stream<Class<?>> byteClassesProvider() {
        return Stream.of(Byte.class, byte.class);
    }

    private static Stream<Class<?>> shortClassesProvider() {
        return Stream.of(Short.class, short.class);
    }

    private static Stream<Class<?>> integerClassesProvider() {
        return Stream.of(Integer.class, int.class);
    }

    private static Stream<Class<?>> longClassesProvider() {
        return Stream.of(Long.class, long.class);
    }

    private static Stream<Class<?>> allStringClassesProvider() {
        return Stream.of(String.class, Serializable.class, Comparable.class, CharSequence.class, Object.class);
    }

    private static Stream<Class<?>> allTypesProvider() {
        return Stream.of(
                booleanClassesProvider(),
                byteClassesProvider(),
                shortClassesProvider(),
                integerClassesProvider(),
                longClassesProvider(),
                Stream.of(String.class, byte[].class, Byte[].class)
        ).flatMap(i -> i);
    }

    private static void assertEqualsAndAssignable(Object expected, Object actual, String msg) throws ReflectiveOperationException {
        final Class<?> clazz = expected.getClass();
        assertTrue(ClassUtils.isAssignable(clazz, actual.getClass()), msg);
        if (clazz.isArray() && clazz.getComponentType().isPrimitive()) {
            assertEquals(clazz, actual.getClass(), msg);
            Method m = Assertions.class.getMethod("assertArrayEquals", clazz, clazz, String.class);
            m.invoke(null, expected, actual, msg);
        } else if (!clazz.isArray()) {
            assertEquals(expected, actual, msg);
        } else {
            assertTrue(Objects.deepEquals(expected, actual), msg);
        }
    }

    @MethodSource("allStringClassesProvider")
    @ParameterizedTest
    void assertMappingShouldReturnStringForEligibleTypes(Class<?> stringClass) throws Exception {
        final String testString = "ThisIsTest String!@#$";
        final ByteBuffer buffer = ByteBuffer.wrap(testString.getBytes());
        assertMappingWorks(buffer, stringClass, true, 0, testString.length(), testString);
    }

    @MethodSource("allTypesProvider")
    @ParameterizedTest
    void assertMappingShouldNotReturnNullForBE(Class<?> clazz) {
        final FieldMapper mapper = getMapper(clazz);
        final Object obj = mapper.getValue(ByteBuffer.allocate(Long.BYTES), true, 0, 1);
        assertNotNull(obj);
    }

    @MethodSource("allTypesProvider")
    @ParameterizedTest
    void assertMappingShouldNotReturnNullForLE(Class<?> clazz) {
        final FieldMapper mapper = getMapper(clazz);
        final Object obj = mapper.getValue(ByteBuffer.allocate(Long.BYTES), false, 0, 1);
        assertNotNull(obj);
    }

    @MethodSource("byteClassesProvider")
    @ParameterizedTest
    void assertMappingShouldCheckSizeForByte(Class<?> clazz) {
        final FieldMapper mapper = getMapper(clazz);
        final Exception exception = assertThrows(TooSmallDatatypeException.class, () ->
                mapper.getValue(ByteBuffer.allocate(Long.BYTES), true, 0, Integer.MAX_VALUE)
        );
        assertTrue(exception.getMessage().contains("maximum allowed size is 1"));
    }

    @MethodSource("shortClassesProvider")
    @ParameterizedTest
    void assertMappingShouldCheckSizeForShort(Class<?> clazz) {
        final FieldMapper mapper = getMapper(clazz);
        final Exception exception = assertThrows(TooSmallDatatypeException.class, () ->
                mapper.getValue(ByteBuffer.allocate(Long.BYTES), true, 0, Integer.MAX_VALUE)
        );
        assertTrue(exception.getMessage().contains("maximum allowed size is 2"));
    }

    @MethodSource("integerClassesProvider")
    @ParameterizedTest
    void assertMappingShouldCheckSizeForInteger(Class<?> clazz) {
        final FieldMapper mapper = getMapper(clazz);
        final Exception exception = assertThrows(TooSmallDatatypeException.class, () ->
                mapper.getValue(ByteBuffer.allocate(Long.BYTES), true, 0, Integer.MAX_VALUE)
        );
        assertTrue(exception.getMessage().contains("maximum allowed size is 4"));
    }

    @MethodSource("longClassesProvider")
    @ParameterizedTest
    void assertMappingShouldCheckSizeForLong(Class<?> clazz) {
        final FieldMapper mapper = getMapper(clazz);
        final Exception exception = assertThrows(TooSmallDatatypeException.class, () ->
                mapper.getValue(ByteBuffer.allocate(Long.BYTES), true, 0, Integer.MAX_VALUE)
        );
        assertTrue(exception.getMessage().contains("maximum allowed size is 8"));
    }

    @Test()
    void assertMappingWorkingForBEString() throws Exception {
        final String testString = "ThisIsTest String!@#$";
        final ByteBuffer buffer = ByteBuffer.wrap(testString.getBytes());
        assertMappingWorks(buffer, String.class, true, 0, testString.length(), testString);
    }

    @Test()
    void assertMappingWorkingForLEString() throws Exception {
        final String testString = "ThisIsTest String!@#$";
        final String reversedString = new StringBuilder(testString).reverse().toString();
        final ByteBuffer buffer = ByteBuffer.wrap(testString.getBytes());
        assertMappingWorks(buffer, String.class, false, 0, testString.length(), reversedString);
    }

    @MethodSource("booleanClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForLEBoolean(Class<?> booleanClass) throws Exception {
        final int typeSize = Byte.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 4)
                .put((byte) 0)
                .put((byte) 1)
                .put(Byte.MAX_VALUE)
                .put((byte) 0xFF)
                .flip();

        assertMappingWorks(buffer, booleanClass, false, 0, typeSize, false);
        assertMappingWorks(buffer, booleanClass, false, typeSize, typeSize, true);
        assertMappingWorks(buffer, booleanClass, false, 2 * typeSize, typeSize, true);
        assertMappingWorks(buffer, booleanClass, false, 3 * typeSize, typeSize, true);
    }

    @MethodSource("booleanClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForBEBoolean(Class<?> booleanClass) throws Exception {
        final int typeSize = Byte.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 4)
                .put((byte) 0)
                .put((byte) 1)
                .put(Byte.MAX_VALUE)
                .put((byte) 0xFF)
                .flip();

        assertMappingWorks(buffer, booleanClass, true, 0, typeSize, false);
        assertMappingWorks(buffer, booleanClass, true, typeSize, typeSize, true);
        assertMappingWorks(buffer, booleanClass, true, 2 * typeSize, typeSize, true);
        assertMappingWorks(buffer, booleanClass, true, 3 * typeSize, typeSize, true);
    }

    @MethodSource("byteClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForLEByte(Class<?> byteClass) throws Exception {
        final int typeSize = Byte.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 4)
                .put((byte) 0)
                .put((byte) 1)
                .put(Byte.MAX_VALUE)
                .put((byte) 0xFF)
                .flip();

        assertMappingWorks(buffer, byteClass, false, 0, typeSize, (byte) 0);
        assertMappingWorks(buffer, byteClass, false, typeSize, typeSize, (byte) 1);
        assertMappingWorks(buffer, byteClass, false, 2 * typeSize, typeSize, Byte.MAX_VALUE);
        assertMappingWorks(buffer, byteClass, false, 3 * typeSize, typeSize, (byte) 0xFF);
    }

    @MethodSource("byteClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForBEByte(Class<?> byteClass) throws Exception {
        final int typeSize = Byte.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 4)
                .put((byte) 0)
                .put((byte) 1)
                .put(Byte.MAX_VALUE)
                .put((byte) 0xFF)
                .flip();

        assertMappingWorks(buffer, byteClass, true, 0, typeSize, (byte) 0);
        assertMappingWorks(buffer, byteClass, true, typeSize, typeSize, (byte) 1);
        assertMappingWorks(buffer, byteClass, true, 2 * typeSize, typeSize, Byte.MAX_VALUE);
        assertMappingWorks(buffer, byteClass, true, 3 * typeSize, typeSize, (byte) 0xFF);
    }

    @MethodSource("shortClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForBEShort(Class<?> byteClass) throws Exception {
        final int typeSize = Short.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 3)
                .putShort((short) 0)
                .putShort((short) 1)
                .putShort(Short.MAX_VALUE)
                .flip();

        assertMappingWorks(buffer, byteClass, true, 0, typeSize, (short) 0);
        assertMappingWorks(buffer, byteClass, true, typeSize, typeSize, (short) 1);
        assertMappingWorks(buffer, byteClass, true, 2 * typeSize, typeSize, Short.MAX_VALUE);
    }

    @MethodSource("shortClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForLEShort(Class<?> byteClass) throws Exception {
        final int typeSize = Short.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 3)
                .putShort((short) 0)
                .putShort((short) 0x0100)
                .putShort((short) 0xFF00)
                .flip();

        assertMappingWorks(buffer, byteClass, false, 0, typeSize, (short) 0);
        assertMappingWorks(buffer, byteClass, false, typeSize, typeSize, (short) 0x0001);
        assertMappingWorks(buffer, byteClass, false, 2 * typeSize, typeSize, (short) 0x00FF);
    }

    @MethodSource("integerClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForBEInteger(Class<?> byteClass) throws Exception {
        final int typeSize = Integer.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 3)
                .putInt(0)
                .putInt(1)
                .putInt(Integer.MAX_VALUE)
                .flip();

        assertMappingWorks(buffer, byteClass, true, 0, typeSize, 0);
        assertMappingWorks(buffer, byteClass, true, typeSize, typeSize, 1);
        assertMappingWorks(buffer, byteClass, true, 2 * typeSize, typeSize, Integer.MAX_VALUE);
    }

    @MethodSource("integerClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForLEInteger(Class<?> byteClass) throws Exception {
        final int typeSize = Integer.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 3)
                .putInt(0)
                .putInt(0x01000000)
                .putInt(0xFFFF0000)
                .flip();

        assertMappingWorks(buffer, byteClass, false, 0, typeSize, 0);
        assertMappingWorks(buffer, byteClass, false, typeSize, typeSize, 1);
        assertMappingWorks(buffer, byteClass, false, 2 * typeSize, typeSize, 0x0000FFFF);
    }

    @MethodSource("longClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForBELong(Class<?> byteClass) throws Exception {
        final int typeSize = Long.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 3)
                .putLong(0)
                .putLong(1)
                .putLong(Long.MAX_VALUE)
                .flip();

        assertMappingWorks(buffer, byteClass, true, 0, typeSize, 0L);
        assertMappingWorks(buffer, byteClass, true, typeSize, typeSize, 1L);
        assertMappingWorks(buffer, byteClass, true, 2 * typeSize, typeSize, Long.MAX_VALUE);
    }

    @MethodSource("longClassesProvider")
    @ParameterizedTest
    void assertMappingWorkingForLELong(Class<?> byteClass) throws Exception {
        final int typeSize = Long.BYTES;
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(typeSize * 3)
                .putLong(0)
                .putLong(0x0100000000000000L)
                .putLong(0xFFFFFFFF00000000L)
                .flip();

        assertMappingWorks(buffer, byteClass, false, 0, typeSize, 0L);
        assertMappingWorks(buffer, byteClass, false, typeSize, typeSize, 1L);
        assertMappingWorks(buffer, byteClass, false, 2 * typeSize, typeSize, 0x00000000FFFFFFFFL);
    }

    @Test
    void assertMappingWorkingForBEByteArray() throws Exception {
        final byte[] first = new byte[]{
                1, 2, 3, 4, 5, 6, 7, 8
        };
        final byte[] second = new byte[]{
                8, 9, 10
        };
        final byte[] third = new byte[]{
                14
        };
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(first.length + second.length + third.length)
                .put(first)
                .put(second)
                .put(third)
                .flip();

        assertMappingWorks(buffer, byte[].class, true, 0, first.length, first);
        assertMappingWorks(buffer, byte[].class, true, first.length, second.length, second);
        assertMappingWorks(buffer, byte[].class, true, first.length + second.length, third.length, third);
    }

    @Test
    void assertMappingWorkingForLEByteArray() throws Exception {
        final byte[] first = new byte[]{
                1, 2, 3, 4, 5, 6, 7, 8
        };
        final byte[] second = new byte[]{
                8, 9, 10
        };
        final byte[] third = new byte[]{
                14
        };
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(first.length + second.length + third.length)
                .put(first)
                .put(second)
                .put(third)
                .flip();

        final byte[] firstReversed = ArrayUtils.clone(first);
        ArrayUtils.reverse(firstReversed);

        final byte[] secondReversed = ArrayUtils.clone(second);
        ArrayUtils.reverse(secondReversed);

        final byte[] thirdReversed = ArrayUtils.clone(third);
        ArrayUtils.reverse(thirdReversed);

        assertMappingWorks(buffer, byte[].class, false, 0, first.length, firstReversed);
        assertMappingWorks(buffer, byte[].class, false, first.length, second.length, secondReversed);
        assertMappingWorks(buffer, byte[].class, false, first.length + second.length, third.length, thirdReversed);
    }

    @Test
    void assertMappingWorkingForBEByteObjectArray() throws Exception {
        final byte[] first = new byte[]{
                1, 2, 3, 4, 5, 6, 7, 8
        };
        final byte[] second = new byte[]{
                8, 9, 10
        };
        final byte[] third = new byte[]{
                14
        };
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(first.length + second.length + third.length)
                .put(first)
                .put(second)
                .put(third)
                .flip();

        assertMappingWorks(buffer, Byte[].class, true, 0, first.length, ArrayUtils.toObject(first));
        assertMappingWorks(buffer, Byte[].class, true, first.length, second.length, ArrayUtils.toObject(second));
        assertMappingWorks(buffer, Byte[].class, true, first.length + second.length, third.length, ArrayUtils.toObject(third));
    }

    @Test
    void assertMappingWorkingForLEByteObjectArray() throws Exception {
        final byte[] first = new byte[]{
                1, 2, 3, 4, 5, 6, 7, 8
        };
        final byte[] second = new byte[]{
                8, 9, 10
        };
        final byte[] third = new byte[]{
                14
        };
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(first.length + second.length + third.length)
                .put(first)
                .put(second)
                .put(third)
                .flip();

        final Byte[] firstReversed = ArrayUtils.toObject(first);
        ArrayUtils.reverse(firstReversed);

        final Byte[] secondReversed = ArrayUtils.toObject(second);
        ArrayUtils.reverse(secondReversed);

        final Byte[] thirdReversed = ArrayUtils.toObject(third);
        ArrayUtils.reverse(thirdReversed);

        assertMappingWorks(buffer, Byte[].class, false, 0, first.length, firstReversed);
        assertMappingWorks(buffer, Byte[].class, false, first.length, second.length, secondReversed);
        assertMappingWorks(buffer, Byte[].class, false, first.length + second.length, third.length, thirdReversed);
    }

    private void assertMappingWorks(
            ByteBuffer buffer,
            Class<?> clazz,
            boolean isBigEndian,
            int startByte,
            int size,
            Object expectedValue
    ) throws ReflectiveOperationException {
        final FieldMapper mapper = getMapper(clazz);
        final Object mappedValue = mapper.getValue(buffer, isBigEndian, startByte, size);
        final String notEqualMsg = String.format(
                "Mapped %s value is not equal to expected:\nExpected:\t(%s) %s\nActual:  \t(%s) %s",
                isBigEndian,
                expectedValue.getClass().getSimpleName(),
                ArrayUtils.toString(expectedValue),
                mappedValue.getClass().getSimpleName(),
                ArrayUtils.toString(mappedValue)
        );
        assertEqualsAndAssignable(expectedValue, mappedValue, notEqualMsg);
    }

    private FieldMapper getMapper(Class<?> clazz) {
        final FieldMapper mapper = FieldMapperProvider.getMapper(clazz);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(clazz));
        return mapper;
    }
}