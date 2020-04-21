package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.Value;
import eu.trisquare.bytemapper.fieldmapper.FieldMapper;
import eu.trisquare.bytemapper.fieldmapper.FieldMapperProvider;
import eu.trisquare.bytemapper.fieldmapper.TooSmallDatatypeException;
import eu.trisquare.bytemapper.fieldmapper.UnsupportedTypeException;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Collection;

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
     * Maps values to {@link Value} annotated fields of provided object instance.
     * Fields cannot be declared neither static nor final.
     *
     * @param instance   class with fields annotated by {@link Value}
     * @param byteBuffer used as a data source for value mappers
     * @param <T>        type of processed object
     * @return same object with values mapped into annotated fields
     * @throws RuntimeException              when mapper failed to write value into field.
     * @throws IllegalFieldModifierException when fields is either static or final
     * @throws EmptyBufferException          when provided buffer is empty
     * @throws NegativeIndexException        when start byte index is negative
     * @throws InvalidSizeException          when size is lower than 1 byte
     * @throws DataExceedsBufferException    when last byte index exceeds buffer limit
     * @throws UnsupportedTypeException      when unable to determine eligible mapper for given field type
     * @throws TooSmallDatatypeException     when field data type cannot fit requested size
     * @throws ArithmeticException           if the value of mapped value will not exactly fit in a datatype due to
     *                                       signedness conversion.
     */
    public static <T> T mapValues(T instance, ByteBuffer byteBuffer) {
        final Class<?> objectClass = instance.getClass();
        final Collection<Field> annotatedFields = getValueAnnotatedFields(objectClass);
        for (Field field : annotatedFields) {
            mapFieldValue(field, instance, byteBuffer);
        }
        return instance;

    }

    /**
     * Creates new instance of provided class and maps values to {@link Value} annotated fields
     * Fields cannot be declared neither static nor final and class must not be non-static inner
     * class. If last constrain is not suitable for your needs, consider using {@link #mapValues(Object, ByteBuffer)}
     * method instead.
     *
     * @param clazz      with fields annotated by {@link Value} to instantiate. Must not be non-static inner class.
     * @param byteBuffer used as a data source for value mappers
     * @param <T>        type of processed object
     * @return instantiated object with values mapped into annotated fields
     * @throws AbstractClassInstantiationException when provided class is either abstract or interface
     * @throws NoAccessibleConstructorException    when provided class has no public default constructor
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
        final T instance = getInstance(clazz);
        return mapValues(instance, byteBuffer);
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
        final FieldMapper fieldMapper = mapperProvider.getMapper(fieldType);

        final int bufferLimit = byteBuffer.limit();
        checkBufferLimit(bufferLimit);

        final int startByte = valueAnnotation.startByte();
        checkStartByte(startByte);

        final int size = valueAnnotation.size();
        checkSize(startByte, size, bufferLimit);


        final boolean isBigEndian = valueAnnotation.bigEndian();
        final ByteBuffer workingBuffer = byteBuffer.asReadOnlyBuffer();
        final Object mappedValue = fieldMapper.getValue(
                workingBuffer,
                isBigEndian,
                startByte,
                size
        );

        setValue(field, instance, mappedValue);
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
