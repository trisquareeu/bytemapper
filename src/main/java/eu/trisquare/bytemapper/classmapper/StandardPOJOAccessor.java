package eu.trisquare.bytemapper.classmapper;


import eu.trisquare.bytemapper.annotations.ByteMapperConstructor;
import eu.trisquare.bytemapper.annotations.Structure;
import eu.trisquare.bytemapper.annotations.Value;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Provides methods wrapping standard reflection calls
 */
public class StandardPOJOAccessor implements POJOAccessor {

    /**
     * This class is not required to be instantiated, because API is provided as static methods
     */
    public StandardPOJOAccessor() {
        //empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getInstanceUsingDefaultConstructor(Class<T> clazz) {
        final Constructor<T> constructor = getDefaultConstructor(clazz);
        return getInstance(constructor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getInstanceUsingAnnotatedConstructor(Class<T> clazz, List<Object> arguments) {
        final Constructor<T> constructor = getAnnotatedConstructor(clazz);
        return getInstance(constructor, arguments.toArray());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assignValue(Field field, Object instance, Object value) {
        if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
            final String message = String.format(
                    "Unable to set value for field: %s. Mapped field must not be static nor final.",
                    field.getName()
            );
            throw new ClassMappingException(message);
        }
        try {
            field.setAccessible(true);
            field.set(instance, value);
            field.setAccessible(false);
        } catch (Exception e) {
            final String msg = String.format("Unable to set value for field %s.", field.getName());
            throw new ClassMappingException(msg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Parameter> getAnnotatedConstructorParams(Class<?> annotatedClass) {
        final Constructor<?> constructor = getAnnotatedConstructor(annotatedClass);
        return Stream.of(constructor.getParameters()).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAnnotatedConstructor(Class<?> objectClass) {
        final long constructors = Arrays.stream(objectClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(ByteMapperConstructor.class))
                .count();
        return constructors > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Field> getValueAnnotatedFields(Class<?> objectClass) {
        final Predicate<Field> valueAnnotationPredicate = field -> field.isAnnotationPresent(Value.class);
        final Predicate<Field> structureAnnotationPredicate = field -> field.isAnnotationPresent(Structure.class);

        return Stream.of(objectClass.getDeclaredFields())
                .filter(valueAnnotationPredicate.or(structureAnnotationPredicate))
                .collect(Collectors.toList());
    }

    private <T> Constructor<T> getAnnotatedConstructor(Class<T> objectClass) {
        final List<Constructor<?>> annotatedConstructors = Arrays
                .stream(objectClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(ByteMapperConstructor.class))
                .collect(Collectors.toList());
        if (annotatedConstructors.size() != 1) {
            throw new IllegalStateException("Class must have exactly one annotated constructor.");
        }
        @SuppressWarnings("unchecked") //safe
        final Constructor<T> constructor = (Constructor<T>) annotatedConstructors.get(0);
        return constructor;
    }

    /**
     * Returns default constructor of given class
     */
    private <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            final String message = String.format(
                    "Provided class must not be interface nor abstract class: %s",
                    clazz.getSimpleName()
            );
            throw new ClassMappingException(message);
        }
        final Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            final String message = String.format(
                    "Class %s must have default constructor and must be declared in static context",
                    clazz.getSimpleName()
            );
            throw new ClassMappingException(message, e);
        }
        return constructor;
    }

    private <T> T getInstance(Constructor<T> constructor, Object... args) {
        final T instance;
        try {
            constructor.setAccessible(true);
            instance = constructor.newInstance(args);
            constructor.setAccessible(false);
        } catch (Exception e) {
            throw new ClassMappingException("Class cannot be instantiated.", e);
        }
        return instance;
    }

}
