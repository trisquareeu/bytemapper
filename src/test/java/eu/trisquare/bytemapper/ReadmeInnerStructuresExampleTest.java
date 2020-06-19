package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.ByteMapperConstructor;
import eu.trisquare.bytemapper.annotations.Structure;
import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.impl.ByteMapperBuilder;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
class ReadmeInnerStructuresExampleTest {


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
        assertFalse(object.object.booleanValue);
        assertEquals((byte) 0xFF, object.object.byteValue);
        assertEquals(Short.MAX_VALUE, object.object.shortValue);
        assertEquals(Integer.MAX_VALUE, object.object.intValue);
        assertEquals(Long.MAX_VALUE, object.object.longValue);
    }


    //inner classes must be declared static to be instantiated
    private static class DemoObject {
        @Structure(startByte = 0, size = 16)
        private DemoStructure object;

        private DemoObject() {
            //instantiated classes must have default constructor
        }
    }

    //inner classes must be declared static to be instantiated
    private static class DemoStructure {
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
        private DemoStructure(
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
