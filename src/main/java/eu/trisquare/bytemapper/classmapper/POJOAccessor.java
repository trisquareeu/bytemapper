package eu.trisquare.bytemapper.classmapper;

import eu.trisquare.bytemapper.annotations.ByteMapperConstructor;
import eu.trisquare.bytemapper.annotations.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;

public interface POJOAccessor {

    /**
     * Checks if given class has exactly one public constructor annotated with {@link ByteMapperConstructor}
     *
     * @param clazz to check for presence of annotated constructor
     * @return true if exactly one public, annotated constructor is present, false if no public annotated constructor is found
     */
    boolean hasAnnotatedConstructor(Class<?> clazz);

    /**
     * Returns new instance of given class using constructor annotated with {@link ByteMapperConstructor}
     *
     * @param clazz     class to instantiate
     * @param arguments passed to constructor as parameters
     * @param <T>       type of instantiated class
     * @return new instance of given class
     */
    <T> T getInstanceUsingAnnotatedConstructor(Class<T> clazz, List<Object> arguments);

    /**
     * Returns list of parameters of constructor annotated with {@link ByteMapperConstructor}
     *
     * @param clazz with annotated constructor
     * @return list of annotated constructor parameters
     */
    List<Parameter> getAnnotatedConstructorParams(Class<?> clazz);

    /**
     * Returns new instance of given class using default (non-parametrized) constructor1
     *
     * @param clazz class to instantiate
     * @param <T>   type of instantiated class
     * @return new instance of given class
     */
    <T> T getInstanceUsingDefaultConstructor(Class<T> clazz);

    /**
     * Returns list of class fields annotated with {@link Value}
     *
     * @param clazz to look into for annotated fields
     * @return list of annotated fiels
     */
    List<Field> getValueAnnotatedFields(Class<?> clazz);

    /**
     * Sets value into instance's field
     *
     * @param field    to assign value with
     * @param instance that contains given field
     * @param value    to assign
     */
    void assignValue(Field field, Object instance, Object value);

}
