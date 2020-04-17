package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.fieldmapper.NoMapperFoundException;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
class ByteMapperTest {

    private static final String TEST_STRING_VALUE = "1234";
    private static final int TEST_STRING_LENGTH = 4;

    @Test
    void mapValuesShouldThrowExceptionWhenEmptyBuffer() {
        final Exception exception = assertThrows(EmptyBufferException.class, () ->
                ByteMapper.mapValues(TestClasses.ValidMappingClass.class, ByteBuffer.allocate(0))
        );
        assertEquals("Buffer limit must be bigger than 0, but is 0.", exception.getMessage());
    }

    @Test
    void mapValuesShouldThrowExceptionWhenUnsupportedType() {
        final Exception exception = assertThrows(NoMapperFoundException.class, () ->
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
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(TEST_STRING_VALUE.length())
                .put(TEST_STRING_VALUE.getBytes())              //bytes 0-4
                .flip();
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
                "Provided class must have public default constructor " +
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
        final Exception exception = assertThrows(SizeTooSmallException.class, () ->
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
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(20)
                .put((byte) 0x00)                                //0
                .put((byte) 0xFF)                                //1
                .putShort(Short.MAX_VALUE)                      //2
                .putInt(Integer.MAX_VALUE)                      //4
                .putLong(Long.MAX_VALUE)                        //8
                .put(TEST_STRING_VALUE.getBytes())              //16
                .flip();

        final TestClasses.ValidMappingClass object = ByteMapper.mapValues(TestClasses.ValidMappingClass.class, buffer);
        assertFalse(object.booleanValue);
        assertEquals((byte) 0xFF, object.byteValue);
        assertEquals(Short.MAX_VALUE, object.shortValue);
        assertEquals(Integer.MAX_VALUE, object.intValue);
        assertEquals(Long.MAX_VALUE, object.longValue);
        assertEquals(TEST_STRING_VALUE, object.stringValue);
    }

    @Test
    void testMapValuesForExistingObject() {
        final ByteBuffer buffer = (ByteBuffer) ByteBuffer
                .allocate(20)
                .put((byte) 0x00)                                //0
                .put((byte) 0xFF)                                //1
                .putShort(Short.MAX_VALUE)                      //2
                .putInt(Integer.MAX_VALUE)                      //4
                .putLong(Long.MAX_VALUE)                        //8
                .put(TEST_STRING_VALUE.getBytes())              //16
                .flip();

        final TestClasses.ValidMappingClass object = new TestClasses.ValidMappingClass();
        ByteMapper.mapValues(object, buffer);
        assertFalse(object.booleanValue);
        assertEquals((byte) 0xFF, object.byteValue);
        assertEquals(Short.MAX_VALUE, object.shortValue);
        assertEquals(Integer.MAX_VALUE, object.intValue);
        assertEquals(Long.MAX_VALUE, object.longValue);
        assertEquals(TEST_STRING_VALUE, object.stringValue);
    }

    private static class TestClasses {
        private static abstract class AbstractClass {
            public AbstractClass() {
                //empty
            }
        }

        private static class NoPublicDefaultConstructor {
            private NoPublicDefaultConstructor() {
                //empty
            }
        }

        private static class NegativeIndexClass {
            @Value(startByte = -1)
            private boolean booleanValue;

            public NegativeIndexClass() {
                //empty
            }
        }

        private static class NegativeSizeClass {
            @Value(startByte = 0, size = -1)
            private boolean booleanValue;

            public NegativeSizeClass() {
                //empty
            }
        }

        private static class SizeExceedsBuffer {
            @Value(startByte = 0, size = 8)
            private boolean booleanValue;

            public SizeExceedsBuffer() {
                //empty
            }
        }

        private static class FinalField {
            @Value(startByte = 0)
            private final boolean booleanValue = false;

            public FinalField() {
                //empty
            }
        }

        private static class StaticField {
            @Value(startByte = 0)
            private static boolean booleanValue;

            public StaticField() {
                //empty
            }
        }

        private static class UnknownTypeClass {
            @Value(startByte = 0, size = TEST_STRING_LENGTH)
            private Object object;

            public UnknownTypeClass() {
                //empty
            }
        }

        private static class UnsupportedType {
            @Value(startByte = 0, size = 1)
            private Void object;

            public UnsupportedType() {
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

            @Value(startByte = 16, size = TEST_STRING_LENGTH)
            private String stringValue;

            public ValidMappingClass() {
                //empty
            }
        }
    }


}