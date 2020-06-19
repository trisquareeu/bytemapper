package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.Structure;
import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.impl.ByteMapperBuilder;
import eu.trisquare.bytemapper.impl.MappingException;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class ByteMapperAnnotatedFieldsTest {

    private static final String TEST_STRING_VALUE = "1234";

    private final ByteMapper mapper = new ByteMapperBuilder().build();


    @Test
    void mapValuesShouldThrowExceptionWhenEmptyBuffer() {
        final ByteBuffer buffer = ByteBuffer.allocate(0);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.ValidMappingClass.class, buffer)
        );
        assertEquals("Buffer limit must be bigger than 0, but is 0.", exception.getMessage());
    }

    @Test
    void mapValuesShouldThrowExceptionWhenUnsupportedType() {
        final ByteBuffer buffer = ByteBuffer.allocate(0);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.UnsupportedType.class, buffer)
        );
        assertEquals("No mapper has been found for class: java.lang.Void", exception.getMessage());
    }

    @Test
    void mapValuesShouldThrowExceptionWhenNegativeIdx() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.NegativeIndexClass.class, buffer)
        );
        assertEquals(
                "Byte index must be positive! (-1 was provided)",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenIdxNotLowerThanBufferSize() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.ValidMappingClass.class, buffer)
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
        TestClasses.UnknownTypeClass obj = mapper.mapValues(TestClasses.UnknownTypeClass.class, buffer);

        assertTrue(obj.object instanceof String);
        assertEquals(
                TEST_STRING_VALUE,
                obj.object
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenNoPublicDefaultConstructor() {
        final ByteBuffer buffer = ByteBuffer.allocate(0);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.NoPublicDefaultConstructor.class, buffer)
        );
        assertEquals(
                "Class NoPublicDefaultConstructor must have default constructor and must be declared in static context",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenAbstract() {
        final ByteBuffer buffer = ByteBuffer.allocate(0);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.AbstractClass.class, buffer)
        );
        assertEquals(
                "Provided class must not be interface nor abstract class: AbstractClass",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenNegativeSize() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.NegativeSizeClass.class, buffer)
        );
        assertEquals(
                "Size should be bigger than 0, but -1 was provided",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenSizeExceedsBuffer() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.SizeExceedsBuffer.class, buffer)
        );
        assertEquals(
                "Last byte index should not exceed buffer limit of 1 bytes, but 8 was calculated",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenFieldIsFinal() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.FinalField.class, buffer)
        );
        assertEquals(
                "Unable to set value for field: booleanValue. Mapped field must not be static nor final.",
                exception.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowExceptionWhenFieldIsStatic() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        final Exception exception = assertThrows(MappingException.class, () ->
                mapper.mapValues(TestClasses.StaticField.class, buffer)
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

        final TestClasses.ValidMappingClass object = mapper.mapValues(TestClasses.ValidMappingClass.class, buffer);
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

    @Test
    void testMapValuesWithStructure() {
        final ByteBuffer buffer = ByteBuffer.allocate(80);
        buffer.put((byte) 0x00);                          //0
        buffer.put(Byte.MAX_VALUE);                       //1
        buffer.putShort(Short.MAX_VALUE);                 //2
        buffer.putInt(Integer.MAX_VALUE);                 //4
        buffer.putLong(Long.MAX_VALUE);                   //8
        buffer.put(TEST_STRING_VALUE.getBytes());         //16
        buffer.putLong(Long.MAX_VALUE);                   //20
        buffer.putDouble(Double.MAX_VALUE);               //28
        buffer.putFloat(Float.MAX_VALUE);                 //36
        buffer.put((byte) 0x01);                          //40
        buffer.put(Byte.MIN_VALUE);                       //41
        buffer.putShort(Short.MIN_VALUE);                 //42
        buffer.putInt(Integer.MIN_VALUE);                 //44
        buffer.putLong(Long.MIN_VALUE);                   //48
        buffer.put(TEST_STRING_VALUE.getBytes());         //56
        buffer.putLong(Long.MIN_VALUE);                   //60
        buffer.putDouble(Double.MIN_VALUE);               //68
        buffer.putFloat(Float.MIN_VALUE);                 //76
        buffer.flip();

        final TestClasses.ValidStructureMappingClass object = mapper.mapValues(TestClasses.ValidStructureMappingClass.class, buffer);
        assertFalse(object.obj1.booleanValue);
        assertEquals(Byte.MAX_VALUE, object.obj1.byteValue);
        assertEquals(Short.MAX_VALUE, object.obj1.shortValue);
        assertEquals(Integer.MAX_VALUE, object.obj1.intValue);
        assertEquals(Long.MAX_VALUE, object.obj1.longValue);
        assertEquals(TEST_STRING_VALUE, object.obj1.stringValue);
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE), object.obj1.bigInteger);
        assertEquals(Double.MAX_VALUE, object.obj1.doubleValue);
        assertEquals(Float.MAX_VALUE, object.obj1.floatValue);
        assertTrue(object.obj2.booleanValue);
        assertEquals(Byte.MIN_VALUE, object.obj2.byteValue);
        assertEquals(Short.MIN_VALUE, object.obj2.shortValue);
        assertEquals(Integer.MIN_VALUE, object.obj2.intValue);
        assertEquals(Long.MIN_VALUE, object.obj2.longValue);
        assertEquals(TEST_STRING_VALUE, object.obj2.stringValue);
        assertEquals(BigInteger.valueOf(Long.MIN_VALUE), object.obj2.bigInteger);
        assertEquals(Double.MIN_VALUE, object.obj2.doubleValue);
        assertEquals(Float.MIN_VALUE, object.obj2.floatValue);
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

        private static class ValidStructureMappingClass {

            @Structure(startByte = 0, size = 40)
            private ValidMappingClass obj1;

            @Structure(startByte = 40, size = 40)
            private ValidMappingClass obj2;

            private ValidStructureMappingClass() {
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