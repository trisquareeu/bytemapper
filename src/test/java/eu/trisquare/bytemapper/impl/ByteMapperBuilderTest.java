package eu.trisquare.bytemapper.impl;

import eu.trisquare.bytemapper.ByteMapper;
import eu.trisquare.bytemapper.annotations.Structure;
import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.classmapper.POJOAccessor;
import eu.trisquare.bytemapper.fieldmapper.FieldMapperProvider;
import eu.trisquare.bytemapper.fieldmapper.StructureMapperProvider;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ByteMapperBuilderTest {

    @Test
    void buildShouldReturnByteMapper() {
        ByteMapper mapper = new ByteMapperBuilder().build();
        assertNotNull(mapper);
    }

    @Test
    void buildShouldReturnByteMapperWhenCustomFieldMapperProvider() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 0);
        buffer.flip();
        final FieldMapperProvider customFieldMapperProvider = clazz -> {
            throw new NotImplementedException("customFieldMapperProvider");
        };

        final ByteMapper mapper = new ByteMapperBuilder()
                .withFieldMapperProvider(customFieldMapperProvider)
                .build();
        assertNotNull(mapper);

        final Throwable t = assertThrows(
                NotImplementedException.class,
                () -> mapper.mapValues(TestClass.class, buffer)
        );
        assertEquals("customFieldMapperProvider", t.getMessage());
    }

    @Test
    void buildShouldReturnByteMapperWhenCustomPOJOAccessor() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 0);
        buffer.flip();
        final POJOAccessor customPOJOAccessor = new POJOAccessor() {
            @Override
            public boolean hasAnnotatedConstructor(Class<?> objectClass) {
                throw new NotImplementedException("customPOJOAccessor");
            }

            @Override
            public <T> T getInstanceUsingAnnotatedConstructor(Class<T> clazz, List<Object> arguments) {
                throw new NotImplementedException("customPOJOAccessor");
            }

            @Override
            public List<Parameter> getAnnotatedConstructorParams(Class<?> annotatedClass) {
                throw new NotImplementedException("customPOJOAccessor");
            }

            @Override
            public <T> T getInstanceUsingDefaultConstructor(Class<T> clazz) {
                throw new NotImplementedException("customPOJOAccessor");
            }

            @Override
            public List<Field> getValueAnnotatedFields(Class<?> objectClass) {
                throw new NotImplementedException("customPOJOAccessor");
            }

            @Override
            public void assignValue(Field field, Object instance, Object value) {
                throw new NotImplementedException("customPOJOAccessor");
            }
        };

        final ByteMapper mapper = new ByteMapperBuilder()
                .withPOJOAccessor(customPOJOAccessor)
                .build();
        assertNotNull(mapper);

        final Throwable t = assertThrows(
                NotImplementedException.class,
                () -> mapper.mapValues(TestClass.class, buffer)
        );
        assertEquals("customPOJOAccessor", t.getMessage());
    }

    @Test
    void buildShouldReturnByteMapperWhenCustomStructureMapperProvider() {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 0);
        buffer.flip();
        final StructureMapperProvider customStructureMapperProvider = clazz -> {
            throw new NotImplementedException("customStructureMapperProvider");
        };

        final ByteMapper mapper = new ByteMapperBuilder()
                .withStructureMapperProvider(customStructureMapperProvider)
                .build();
        assertNotNull(mapper);

        final Throwable t = assertThrows(
                NotImplementedException.class,
                () -> mapper.mapValues(TestStructure.class, buffer)
        );
        assertEquals("customStructureMapperProvider", t.getMessage());
    }


    @SuppressWarnings("unused")
    private static class TestStructure {
        @Structure(startByte = 1, size = 1)
        private TestClass testClass;
    }

    @SuppressWarnings("unused")
    private static class TestClass {
        @Value(startByte = 0)
        private byte val;
    }

}
