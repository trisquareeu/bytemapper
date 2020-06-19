package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.ByteMapperConstructor;
import eu.trisquare.bytemapper.annotations.Structure;
import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.impl.ByteMapperBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class ByteMapperAnnotatedConstructorTest {

    private static final String TEST_STRING_VALUE = "1234";

    private final ByteMapper mapper = new ByteMapperBuilder().build();


    @Test
    void mapValuesShouldThrowWhenTwoAnnotatedConstructors() {
        Exception e = assertThrows(
                IllegalStateException.class,
                () -> mapper.mapValues(TestClasses.TwoAnnotatedConstructors.class, null)
        );
        assertEquals(
                "Class must have exactly one annotated constructor.",
                e.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowWhenNoAnnotatedParameter() {
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.mapValues(TestClasses.NoAnnotatedParameter.class, null)
        );
        assertEquals(
                "Not annotated parameter in annotated constructor.",
                e.getMessage()
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

        private static class ValidStructureMappingClass {

            private final ValidMappingClass obj1;

            private final ValidMappingClass obj2;

            @ByteMapperConstructor
            private ValidStructureMappingClass(
                    @Structure(startByte = 0, size = 40) ValidMappingClass obj1,
                    @Structure(startByte = 40, size = 40) ValidMappingClass obj2
            ) {
                this.obj1 = obj1;
                this.obj2 = obj2;
            }
        }

        private static class ValidMappingClass {

            private final boolean booleanValue;
            private final byte byteValue;
            private final short shortValue;
            private final int intValue;
            private final long longValue;
            private final String stringValue;
            private final BigInteger bigInteger;
            private final double doubleValue;
            private final float floatValue;

            @ByteMapperConstructor
            public ValidMappingClass(
                    @Value(startByte = 0) boolean booleanValue,
                    @Value(startByte = 1) byte byteValue,
                    @Value(startByte = 2, size = 2) short shortValue,
                    @Value(startByte = 4, size = 4) int intValue,
                    @Value(startByte = 8, size = 8) long longValue,
                    @Value(startByte = 16, size = 4) String stringValue,
                    @Value(startByte = 20, size = 8) BigInteger bigInteger,
                    @Value(startByte = 28, size = 8) double doubleValue,
                    @Value(startByte = 36, size = 4) float floatValue
            ) {
                this.booleanValue = booleanValue;
                this.byteValue = byteValue;
                this.shortValue = shortValue;
                this.intValue = intValue;
                this.longValue = longValue;
                this.stringValue = stringValue;
                this.bigInteger = bigInteger;
                this.doubleValue = doubleValue;
                this.floatValue = floatValue;
            }
        }

        private static class TwoAnnotatedConstructors {

            @ByteMapperConstructor
            private TwoAnnotatedConstructors(Object o1) {
                //empty
            }

            @ByteMapperConstructor
            private TwoAnnotatedConstructors(Object o1, Object o2) {
                //empty
            }
        }

        private static class NoAnnotatedParameter {

            @ByteMapperConstructor
            private NoAnnotatedParameter(Object o1) {
                //empty
            }

        }
    }


}
