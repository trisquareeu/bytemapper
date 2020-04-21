package eu.trisquare.bytemapper.fieldmapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FieldMapperProviderTest {

    private static Stream<Class<?>> classesProvider() {
        return Stream.of(
                String.class,
                BigInteger.class,
                Double.class, double.class,
                Float.class, float.class,
                Long.class, long.class,
                Integer.class, int.class,
                Short.class, short.class,
                Boolean.class, boolean.class,
                Byte[].class, byte[].class
        );
    }

    private final FieldMapperProvider mapperProvider = new FieldMapperProvider();


    @Test
    void getMapperShouldThrowForUnsupportedType(){
        Exception e = assertThrows(
                UnsupportedTypeException.class,
                () -> mapperProvider.getMapper(Class.class)
        );
        assertEquals("No mapper has been found for class: java.lang.Class", e.getMessage());
    }

    @MethodSource("classesProvider")
    @ParameterizedTest
    void getMapperShouldReturnMapper(Class<?> clazz) {
        assertNotNull(mapperProvider.getMapper(clazz));
    }


    @Test
    void getMapperShouldReturnStringMapperForObject(){
        final FieldMapper mapper = mapperProvider.getMapper(Object.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(String.class));
    }

    @Test
    void getMapperShouldReturnBigIntegerMapperForNumber(){
        final FieldMapper mapper = mapperProvider.getMapper(Number.class);
        assertNotNull(mapper);
        assertTrue(mapper.isEligible(BigInteger.class));
    }
}