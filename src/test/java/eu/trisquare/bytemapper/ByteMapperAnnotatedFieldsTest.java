package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.fieldmapper.UnsupportedTypeException;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class ByteMapperAnnotatedFieldsTest {

    private static final String TEST_STRING_VALUE = "1234";

    @Test
    void mapValuesShouldThrowExceptionWhenEmptyBuffer() {
        final Exception exception = assertThrows(EmptyBufferException.class, () ->
                ByteMapper.mapValues(TestClasses.ValidMappingClass.class, ByteBuffer.allocate(0))
        );
        assertEquals("Buffer limit must be bigger than 0, but is 0.", exception.getMessage());
    }

    @Test
    void mapValuesShouldThrowExceptionWhenUnsupportedType() {
        final Exception exception = assertThrows(UnsupportedTypeException.class, () ->
                ByteMapper.mapValues(TestClasses.UnsupportedType.class, ByteBuffer.allocate(0))
        );
        assertEquals("No mapper has been found for class: java.lang.Void", exception.getMessage());
    }

    @Test
    void mapValuesShouldThrowExceptionWhenNegativeIdx() {
        final Exception exception = assertThrows(NegativeIndexException.class, () ->
                ByteMapper.mapValues(TestClasses.NegativeIndexClass.class, ByteBuffer.allocate(1))
        );
        assertEquals(
                "Byte index must be positive! (-1 was provided)",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenIdxNotLowerThanBufferSize() {
        final Exception exception = assertThrows(DataExceedsBufferException.class, () ->
                ByteMapper.mapValues(TestClasses.ValidMappingClass.class, ByteBuffer.allocate(1))
        );
        assertEquals(
                "Last byte index should not exceed buffer limit of 1 bytes, but 2 was calculated",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldMapAsStringWhenObjectProvided() {
        final ByteBuffer buffer = ByteBuffer.allocate(TEST_STRING_VALUE.length());
        buffer.put(TEST_STRING_VALUE.getBytes());     //bytes 0-4
        buffer.flip();
        TestClasses.UnknownTypeClass obj = ByteMapper.mapValues(TestClasses.UnknownTypeClass.class, buffer);

        assertTrue(obj.object instanceof String);
        assertEquals(
                TEST_STRING_VALUE,
                obj.object
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenNoPublicDefaultConstructor() {
        final Exception exception = assertThrows(NoAccessibleConstructorException.class, () ->
                ByteMapper.mapValues(TestClasses.NoPublicDefaultConstructor.class, ByteBuffer.allocate(0))
        );
        assertEquals(
                "Provided class must have default constructor " +
                        "and must not be non-static nested class: NoPublicDefaultConstructor",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenAbstract() {
        final Exception exception = assertThrows(AbstractClassInstantiationException.class, () ->
                ByteMapper.mapValues(TestClasses.AbstractClass.class, ByteBuffer.allocate(0))
        );
        assertEquals(
                "Provided class must not be interface nor abstract class: AbstractClass",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenNegativeSize() {
        final Exception exception = assertThrows(InvalidSizeException.class, () ->
                ByteMapper.mapValues(TestClasses.NegativeSizeClass.class, ByteBuffer.allocate(1))
        );
        assertEquals(
                "Size should be bigger than 0, but -1 was provided",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenSizeExceedsBuffer() {
        final Exception exception = assertThrows(DataExceedsBufferException.class, () ->
                ByteMapper.mapValues(TestClasses.SizeExceedsBuffer.class, ByteBuffer.allocate(1))
        );
        assertEquals(
                "Last byte index should not exceed buffer limit of 1 bytes, but 8 was calculated",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenFieldIsFinal() {
        final Exception exception = assertThrows(IllegalFieldModifierException.class, () ->
                ByteMapper.mapValues(TestClasses.FinalField.class, ByteBuffer.allocate(1))
        );
        assertEquals(
                "Unable to set value for field: booleanValue. Mapped field must not be static nor final.",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenFieldIsStatic() {
        final Exception exception = assertThrows(IllegalFieldModifierException.class, () ->
                ByteMapper.mapValues(TestClasses.StaticField.class, ByteBuffer.allocate(1))
        );
        assertEquals(
                "Unable to set value for field: booleanValue. Mapped field must not be static nor final.",
                exception.getMessage()
        );
    }

    @Test
    void testMapValues() {
        final ByteBuffer buffer = ByteBuffer.allocate(40);
        buffer.put((byte) 0x00);                          //0
        buffer.put(Byte.MAX_VALUE);                       //1
        buffer.putShort(Short.MAX_VALUE);                 //2
        buffer.putInt(Integer.MAX_VALUE);                 //4
        buffer.putLong(Long.MAX_VALUE);                   //8
        buffer.put(TEST_STRING_VALUE.getBytes());         //16
        buffer.putLong(Long.MAX_VALUE);                   //20
        buffer.putDouble(Double.MAX_VALUE);               //28
        buffer.putFloat(Float.MAX_VALUE);                 //36
        buffer.flip();

        final TestClasses.ValidMappingClass object = ByteMapper.mapValues(TestClasses.ValidMappingClass.class, buffer);
        assertFalse(object.booleanValue);
        assertEquals(Byte.MAX_VALUE, object.byteValue);
        assertEquals(Short.MAX_VALUE, object.shortValue);
        assertEquals(Integer.MAX_VALUE, object.intValue);
        assertEquals(Long.MAX_VALUE, object.longValue);
        assertEquals(TEST_STRING_VALUE, object.stringValue);
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE), object.bigInteger);
        assertEquals(Double.MAX_VALUE, object.doubleValue);
        assertEquals(Float.MAX_VALUE, object.floatValue);
    }

    @SuppressWarnings("unused")
    private static class TestClasses {
        private static abstract class AbstractClass {
            private AbstractClass() {
                //empty
            }
        }

        private static class NoPublicDefaultConstructor {
            private NoPublicDefaultConstructor(Object o1) {
                //empty
            }
        }

        private static class NegativeIndexClass {
            @Value(startByte = -1)
            private boolean booleanValue;

            private NegativeIndexClass() {
                //empty
            }
        }

        private static class NegativeSizeClass {
            @Value(startByte = 0, size = -1)
            private boolean booleanValue;

            private NegativeSizeClass() {
                //empty
            }
        }

        private static class SizeExceedsBuffer {
            @Value(startByte = 0, size = 8)
            private boolean booleanValue;

            private SizeExceedsBuffer() {
                //empty
            }
        }

        private static class FinalField {
            @Value(startByte = 0)
            private final boolean booleanValue = false;

            private FinalField() {
                //empty
            }
        }

        private static class StaticField {
            @Value(startByte = 0)
            private static boolean booleanValue;

            private StaticField() {
                //empty
            }
        }

        private static class UnknownTypeClass {
            @Value(startByte = 0, size = 4)
            private Object object;

            private UnknownTypeClass() {
                //empty
            }
        }

        private static class UnsupportedType {
            @Value(startByte = 0)
            private Void object;

            private UnsupportedType() {
                //empty
            }
        }

        private static class UnsupportedConversion {

            @Value(startByte = 0)
            private boolean booleanValue;

            private UnsupportedConversion() {
                //empty
            }

        }

        private static class ValidMappingClass {

            @Value(startByte = 0)
            private boolean booleanValue;

            @Value(startByte = 1)
            private byte byteValue;

            @Value(startByte = 2, size = 2)
            private short shortValue;

            @Value(startByte = 4, size = 4)
            private int intValue;

            @Value(startByte = 8, size = 8)
            private long longValue;

            @Value(startByte = 16, size = 4)
            private String stringValue;

            @Value(startByte = 20, size = 8)
            private BigInteger bigInteger;

            @Value(startByte = 28, size = 8)
            private double doubleValue;

            @Value(startByte = 36, size = 4)
            private float floatValue;

            private ValidMappingClass() {
                //empty
            }
        }
    }


}