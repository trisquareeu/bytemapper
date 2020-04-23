package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.fieldmapper.FieldMapper;
import eu.trisquare.bytemapper.fieldmapper.FieldMapperProvider;
import eu.trisquare.bytemapper.fieldmapper.TooSmallDatatypeException;
import eu.trisquare.bytemapper.fieldmapper.UnsupportedTypeException;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static eu.trisquare.bytemapper.ReflectionHelper.*;

/**
 * Handles deserialization of raw byte data into Plain Old Java Objects (POJO).
 * It handles object instantiation and mapping values from bytes into annotated fields.
 */
public class ByteMapper {

    /**
     * Mapper provider instance
     */
    private static final FieldMapperProvider mapperProvider = new FieldMapperProvider();

    /**
     * This class is not required to be instantiated, because public API is provided as static methods
     */
    private ByteMapper() {
        //empty
    }

    /**
     * Creates new instance of provided class and maps values to {@link Value} annotated fields
     * Fields cannot be declared neither static nor final and class must not be non-static inner
     * class.
     *
     * @param clazz      with fields annotated by {@link Value} to instantiate. Must not be non-static inner class.
     * @param byteBuffer used as a data source for value mappers
     * @param <T>        type of processed object
     * @return instantiated object with values mapped into annotated fields
     * @throws AbstractClassInstantiationException when provided class is either abstract or interface
     * @throws NoAccessibleConstructorException    when provided class has no default constructor
     * @throws IllegalFieldModifierException       when fields is either static or final
     * @throws RuntimeException                    when mapper failed to write value into field or when class cannot be instantiated
     * @throws EmptyBufferException                when provided buffer is empty
     * @throws NegativeIndexException              when start byte index is negative
     * @throws InvalidSizeException                when size is lower than 1 byte
     * @throws DataExceedsBufferException          when last byte index exceeds buffer limit
     * @throws UnsupportedTypeException            when unable to determine eligible mapper for given field type
     * @throws TooSmallDatatypeException           when field data type cannot fit requested size
     * @throws ArithmeticException                 if the value of mapped value will not exactly fit in a datatype due to
     *                                             signedness conversion.
     */
    public static <T> T mapValues(Class<T> clazz, ByteBuffer byteBuffer) {
        final T instance;
        if (hasAnnotatedConstructor(clazz)) {
            final List<Object> constructorArgs = getAnnotatedConstructorParams(clazz)
                    .stream()
                    .map(parameter -> mapParameterValue(parameter, byteBuffer))
                    .collect(Collectors.toList());
            instance = getInstanceUsingAnnotatedConstructor(clazz, constructorArgs);
        } else {
            instance = getInstanceUsingDefaultConstructor(clazz);
            final Class<?> objectClass = instance.getClass();
            final Collection<Field> annotatedFields = getValueAnnotatedFields(objectClass);
            for (Field field : annotatedFields) {
                mapFieldValue(field, instance, byteBuffer);
            }
        }
        return instance;
    }


    /**
     * Maps value into single, annotated fields.
     *
     * @throws IllegalFieldModifierException when fields is either static or final
     * @throws EmptyBufferException          when provided buffer is empty
     * @throws NegativeIndexException        when start byte index is negative
     * @throws InvalidSizeException          when size is lower than 1 byte
     * @throws DataExceedsBufferException    when last byte index exceeds buffer limit
     * @throws RuntimeException              when mapper failed to write value into field.
     * @throws UnsupportedTypeException      when unable to determine eligible mapper for given field type
     * @throws TooSmallDatatypeException     when field data type cannot fit requested size
     * @throws ArithmeticException           if the value of mapped value will not exactly fit in a datatype due to
     *                                       signedness conversion.
     */
    private static void mapFieldValue(Field field, Object instance, ByteBuffer byteBuffer) {
        final Value valueAnnotation = field.getAnnotation(Value.class);
        final Class<?> fieldType = field.getType();
        final Object mappedValue = mapValue(fieldType, valueAnnotation, byteBuffer);
        setValue(field, instance, mappedValue);
    }

    private static Object mapParameterValue(Parameter parameter, ByteBuffer buffer) {
        final Value valueAnnotation = parameter.getDeclaredAnnotation(Value.class);
        if (valueAnnotation == null) {
            throw new IllegalArgumentException("Not annotated parameter in annotated constructor.");
        }
        final Class<?> parameterType = parameter.getType();
        return mapValue(parameterType, valueAnnotation, buffer);
    }


    private static Object mapValue(Class<?> dataType, Value valueAnnotation, ByteBuffer buffer) {
        final FieldMapper fieldMapper = mapperProvider.getMapper(dataType);

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
     *
     * @throws EmptyBufferException when above condition is not met
     */
    private static void checkBufferLimit(int bufferLimit) {
        if (bufferLimit < 1) {
            throw new EmptyBufferException(bufferLimit);
        }
    }

    /**
     * Checks if start byte index is bigger than zero
     *
     * @throws NegativeIndexException when above condition is not met
     */
    private static void checkStartByte(int startByte) {
        if (startByte < 0) {
            throw new NegativeIndexException(startByte);
        }
    }

    /**
     * Checks if size is bigger than zero and if last byte index does
     * not exceed buffer limit.
     *
     * @throws InvalidSizeException       when size is lower than 1 byte
     * @throws DataExceedsBufferException when last byte index is bigger than buffer limit
     */
    private static void checkSize(int startByte, int size, int bufferLimit) {
        if (size < 1) {
            throw new InvalidSizeException(size);
        }

        if (startByte + size > bufferLimit) {
            throw new DataExceedsBufferException(startByte, size, bufferLimit);
        }
    }


}
