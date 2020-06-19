package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.ByteMapperConstructor;
import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.impl.ByteMapperBuilder;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
class ReadmeAnnotatedConstructorTest {

    @Test
    void testExample() {
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x00);                               //bytes 0 to 1
        buffer.put((byte) 0xFF);                               //bytes 1 to 2
        buffer.putShort(Short.MAX_VALUE);                      //bytes 2 to 4
        buffer.putInt(Integer.MAX_VALUE);                      //bytes 4 to 8
        buffer.putLong(Long.MAX_VALUE);                        //bytes 8 to 16
        buffer.flip();

        //Instantiate object from ByteBuffer content
        final DemoObject object = new ByteMapperBuilder().build().mapValues(DemoObject.class, buffer);
        assertNotNull(object);
        assertFalse(object.booleanValue);
        assertEquals((byte) 0xFF, object.byteValue);
        assertEquals(Short.MAX_VALUE, object.shortValue);
        assertEquals(Integer.MAX_VALUE, object.intValue);
        assertEquals(Long.MAX_VALUE, object.longValue);
    }

    //inner classes must be declared static to be instantiated
    private static class DemoObject {
        /**
         * Value mapped from byte 0 to 1.
         */
        private final boolean booleanValue;

        /**
         * Value mapped from byte 1 to 2
         */
        private final byte byteValue;

        /**
         * Value mapped from byte 2 to 4
         */
        private final short shortValue;

        /**
         * Value mapped from byte 4 to 8
         */
        private final int intValue;

        /**
         * Value mapped from byte 8 to 16
         */
        private final long longValue;

        @ByteMapperConstructor
        private DemoObject(
                @Value(startByte = 0) boolean booleanValue,
                @Value(startByte = 1) byte byteValue,
                @Value(startByte = 2, size = 2) short shortValue,
                @Value(startByte = 4, size = 4) int intValue,
                @Value(startByte = 8, size = 8) long longValue
        ) {
            this.booleanValue = booleanValue;
            this.byteValue = byteValue;
            this.shortValue = shortValue;
            this.intValue = intValue;
            this.longValue = longValue;
        }
    }

}
