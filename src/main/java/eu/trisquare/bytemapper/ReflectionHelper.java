package eu.trisquare.bytemapper;


import eu.trisquare.bytemapper.annotations.ByteMapperConstructor;
import eu.trisquare.bytemapper.annotations.Value;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Provides methods wrapping standard reflection calls
 */
class ReflectionHelper {

    /**
     * This class is not required to be instantiated, because API is provided as static methods
     */
    private ReflectionHelper() {
        //empty
    }

    /**
     * Sets value into instance's field
     *
     * @throws RuntimeException when mapper failed to write value into field.
     */
    static void setValue(Field field, Object instance, Object value) {
        if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
            throw new IllegalFieldModifierException(field);
        }
        try {
            field.setAccessible(true);
            field.set(instance, value);
            field.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to set value for field %s.", field.getName()), e);
        }
    }

    static List<Parameter> getAnnotatedConstructorParams(Class<?> annotatedClass){
        final Constructor<?> constructor = getAnnotatedConstructor(annotatedClass);
        return Stream.of(constructor.getParameters()).collect(Collectors.toList());
    }

    private static <T> Constructor<T> getAnnotatedConstructor(Class<T> objectClass){
        final List<Constructor<?>> annotatedConstructors = Arrays
                .stream(objectClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(ByteMapperConstructor.class))
                .collect(Collectors.toList());
        if(annotatedConstructors.size() != 1){
            throw new IllegalStateException("Class must have exactly one annotated constructor.");
        }
        @SuppressWarnings("unchecked") //safe
        final Constructor<T> constructor = (Constructor<T>) annotatedConstructors.get(0);
        return constructor;
    }

    /**
     * Checks if given class has exactly one public constructor annotated with {@link ByteMapperConstructor}
     * @param objectClass to check for presence of annotated constructor
     * @return true if exactly one public, annotated constructor is present, false if no public annotated constructor is found
     * @throws IllegalArgumentException if class has more than one public annotated constructor
     */
    static boolean hasAnnotatedConstructor(Class<?> objectClass){
        final long constructors = Arrays.stream(objectClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(ByteMapperConstructor.class))
                .count();
        return constructors > 0;
    }

    /**
     * Returns list of class fields annotated with {@link Value}
     */
    static List<Field> getValueAnnotatedFields(Class<?> objectClass) {
        return Stream.of(objectClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Value.class))
                .collect(Collectors.toList());
    }

    /**
     * Returns default constructor of given class
     *
     * @throws AbstractClassInstantiationException when provided class is either abstract or interface
     * @throws NoAccessibleConstructorException    when provided class has no default constructor
     */
    static <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
            throw new AbstractClassInstantiationException(clazz);
        }
        final Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new NoAccessibleConstructorException(clazz);
        }
        return constructor;
    }


    /**
     * Returns new instance of given class
     *
     * @throws RuntimeException when class cannot be instantiated
     */
    static <T> T getInstanceUsingDefaultConstructor(Class<T> clazz) {
        final Constructor<T> constructor = getDefaultConstructor(clazz);
        return getInstance(constructor);
    }

    /**
     * Returns new instance of given class
     *
     * @throws RuntimeException when class cannot be instantiated
     */
    static <T> T getInstanceUsingAnnotatedConstructor(Class<T> clazz, List<Object> arguments) {
        final Constructor<T> constructor = getAnnotatedConstructor(clazz);
        return getInstance(constructor, arguments.toArray());
    }

    static <T> T getInstance(Constructor<T> constructor, Object... args){
        final T instance;
        try {
            constructor.setAccessible(true);
            instance = constructor.newInstance(args);
            constructor.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException("Class cannot be instantiated.", e);
        }
        return instance;
    }

}
