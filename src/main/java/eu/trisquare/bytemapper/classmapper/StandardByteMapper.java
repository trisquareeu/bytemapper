package eu.trisquare.bytemapper.classmapper;

import eu.trisquare.bytemapper.ByteMapper;
import eu.trisquare.bytemapper.annotations.Structure;
import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.fieldmapper.FieldMapper;
import eu.trisquare.bytemapper.fieldmapper.FieldMapperProvider;
import eu.trisquare.bytemapper.fieldmapper.StructureMapper;
import eu.trisquare.bytemapper.fieldmapper.StructureMapperProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Default ByteMapper implementation
 */
public class StandardByteMapper implements ByteMapper {

    /**
     * Field mapper provider instance
     */
    private final FieldMapperProvider fieldMapperProvider;

    /**
     * Structure mapper provider instance
     */
    private final StructureMapperProvider structureMapperProvider;


    /**
     * POJOAccessor instance
     */
    private final POJOAccessor pojoAccessor;

    public StandardByteMapper(
            FieldMapperProvider fieldMapperProvider,
            StructureMapperProvider structureMapperProvider,
            POJOAccessor pojoAccessor
    ) {
        this.fieldMapperProvider = fieldMapperProvider;
        this.structureMapperProvider = structureMapperProvider;
        this.pojoAccessor = pojoAccessor;
    }

    @Override
    public <T> T mapValues(Class<T> clazz, ByteBuffer byteBuffer) {
        final T instance;
        if (pojoAccessor.hasAnnotatedConstructor(clazz)) {
            final List<Object> constructorArgs = pojoAccessor.getAnnotatedConstructorParams(clazz)
                    .stream()
                    .map(parameter -> mapParameterValue(parameter, byteBuffer))
                    .collect(Collectors.toList());
            instance = pojoAccessor.getInstanceUsingAnnotatedConstructor(clazz, constructorArgs);
        } else {
            instance = pojoAccessor.getInstanceUsingDefaultConstructor(clazz);
            final Class<?> objectClass = instance.getClass();
            final Collection<Field> annotatedFields = pojoAccessor.getValueAnnotatedFields(objectClass);
            for (Field field : annotatedFields) {
                mapFieldValue(field, instance, byteBuffer);
            }
        }
        return instance;
    }

    private void mapFieldValue(Field field, Object instance, ByteBuffer byteBuffer) {
        final Class<?> fieldType = field.getType();
        final Value valueAnnotation = field.getAnnotation(Value.class);
        final Structure structureAnnotation = field.getAnnotation(Structure.class);
        final Object outcome;
        if (valueAnnotation != null) {
            outcome = mapValue(fieldType, valueAnnotation, byteBuffer);
        } else {
            outcome = mapValue(fieldType, structureAnnotation, byteBuffer);
        }
        pojoAccessor.assignValue(field, instance, outcome);
    }

    private Object mapParameterValue(Parameter parameter, ByteBuffer buffer) {
        final Class<?> parameterType = parameter.getType();
        final Value valueAnnotation = parameter.getDeclaredAnnotation(Value.class);
        final Structure structureAnnotation = parameter.getDeclaredAnnotation(Structure.class);
        final Object outcome;
        if (valueAnnotation != null) {
            outcome = mapValue(parameterType, valueAnnotation, buffer);
        } else if (structureAnnotation != null) {
            outcome = mapValue(parameterType, structureAnnotation, buffer);
        } else {
            throw new IllegalArgumentException("Not annotated parameter in annotated constructor.");
        }
        return outcome;
    }


    private Object mapValue(Class<?> dataType, Structure structureAnnotation, ByteBuffer buffer) {
        final int startByte = structureAnnotation.startByte();
        final int size = structureAnnotation.size();
        final StructureMapper mapper = structureMapperProvider.getStructureMapper(dataType);
        return mapper.getValue(buffer, dataType, startByte, size);
    }


    private Object mapValue(Class<?> dataType, Value valueAnnotation, ByteBuffer buffer) {
        final FieldMapper fieldMapper = fieldMapperProvider.getMapper(dataType);

        final int bufferLimit = buffer.limit();
        checkBufferLimit(bufferLimit);

        final int startByte = valueAnnotation.startByte();
        checkStartByte(startByte);

        final int size = valueAnnotation.size();
        checkSize(startByte, size, bufferLimit);

        final boolean isBigEndian = valueAnnotation.bigEndian();
        final ByteBuffer workingBuffer = buffer.asReadOnlyBuffer();
        return fieldMapper.getValue(
                workingBuffer,
                isBigEndian,
                startByte,
                size
        );
    }

    /**
     * Checks if buffer limit is bigger than zero
     */
    private void checkBufferLimit(int bufferLimit) {
        if (bufferLimit < 1) {
            final String message = String.format(
                    "Buffer limit must be bigger than 0, but is %d.",
                    bufferLimit
            );
            throw new ClassMappingException(message);
        }
    }

    /**
     * Checks if start byte index is bigger than zero
     */
    private void checkStartByte(int startByte) {
        if (startByte < 0) {
            final String message = String.format(
                    "Byte index must be positive! (%d was provided)",
                    startByte
            );
            throw new ClassMappingException(message);
        }
    }

    /**
     * Checks if size is bigger than zero and if last byte index does
     * not exceed buffer limit.
     */
    private void checkSize(int startByte, int size, int bufferLimit) {
        if (size < 1) {
            final String message = String.format(
                    "Size should be bigger than 0, but %d was provided",
                    size
            );
            throw new ClassMappingException(message);
        }

        if (startByte + size > bufferLimit) {
            final String message = String.format(
                    "Last byte index should not exceed buffer limit of %d bytes, but %d was calculated",
                    bufferLimit, startByte + size
            );
            throw new ClassMappingException(message);
        }
    }

}
