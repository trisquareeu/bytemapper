package eu.trisquare.bytemapper;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Provides methods wrapping standard reflection calls
 */
public class ReflectionHelper {

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
        try {
            field.set(instance, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(String.format("Unable to set value for field %s. ", field.getName()), e);
        }
    }

    /**
     * Returns list of class fields annotated with {@param annotation}
     */
    static List<Field> getAnnotatedFields(Class<?> objectClass, Class<? extends Annotation> annotation) {
        return Stream.of(objectClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Returns default constructor of given class
     *
     * @throws AbstractClassInstantiationException when provided class is either abstract or interface
     * @throws NoAccessibleConstructorException    when provided class has no public default constructor
     */
    static <T> Constructor<T> getConstructor(Class<T> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
            throw new AbstractClassInstantiationException(clazz);
        }
        final Constructor<T> constructor;
        try {
            constructor = clazz.getConstructor();
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
    static <T> T getInstance(Class<T> clazz) {
        final Constructor<T> constructor = getConstructor(clazz);
        final T instance;
        try {
            constructor.setAccessible(true);
            instance = constructor.newInstance();
            constructor.setAccessible(false);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Class cannot be instantiated: " + clazz.getSimpleName(), e);
        }
        return instance;
    }

}
