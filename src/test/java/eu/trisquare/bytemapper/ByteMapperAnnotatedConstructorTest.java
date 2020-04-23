package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.ByteMapperConstructor;
import eu.trisquare.bytemapper.annotations.Value;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class ByteMapperAnnotatedConstructorTest {
    private static final String TEST_STRING_VALUE = "1234";

    @Test
    void mapValuesShouldThrowWhenTwoAnnotatedConstructors(){
        Exception e = assertThrows(
                IllegalStateException.class,
                () -> ByteMapper.mapValues(TestClasses.TwoAnnotatedConstructors.class, null)
        );
        assertEquals(
                "Class must have exactly one annotated constructor.",
                e.getMessage()
        );
    }

    @Test
    void mapValuesShouldThrowWhenNoAnnotatedParameter(){
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> ByteMapper.mapValues(TestClasses.NoAnnotatedParameter.class, null)
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
    private static class TestClasses{
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
