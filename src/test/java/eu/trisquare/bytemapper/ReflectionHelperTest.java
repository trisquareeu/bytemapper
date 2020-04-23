package eu.trisquare.bytemapper;

import eu.trisquare.bytemapper.annotations.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionHelperTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "publicInt", "protectedInt", "defaultInt", "privateInt"
    })
    void setValueShouldSetValueForAccessibleFields(String fieldName) throws Exception {
        final TestClass instance = new TestClass();
        Field f = TestClass.class.getDeclaredField(fieldName);
        ReflectionHelper.setValue(f, instance, Integer.MAX_VALUE);
        try {
            f.setAccessible(true);
            assertEquals(Integer.MAX_VALUE, f.get(instance));
        } finally {
            f.setAccessible(false);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "publicFinalInt", "protectedFinalInt", "defaultFinalInt", "privateFinalInt",
            "publicStaticInt", "protectedStaticInt", "defaultStaticInt", "privateStaticInt",
            "publicStaticFinalInt", "protectedStaticFinalInt", "defaultStaticFinalInt", "privateStaticFinalInt"
    })
    void setValueShouldThrowForNotAccessibleFields(String fieldName) throws Exception {
        final TestClass instance = new TestClass();
        Field f = TestClass.class.getDeclaredField(fieldName);
        Exception e = assertThrows(
                IllegalFieldModifierException.class,
                () -> ReflectionHelper.setValue(f, instance, Integer.MAX_VALUE)
        );
        assertEquals(
                "Unable to set value for field: " + fieldName + ". Mapped field must not be static nor final.",
                e.getMessage()
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "publicInt", "protectedInt", "defaultInt", "privateInt"
    })
    void setValueShouldThrowWhenUnassignableType(String fieldName) throws Exception {
        final TestClass instance = new TestClass();
        Field f = TestClass.class.getDeclaredField(fieldName);
        Exception e = assertThrows(
                RuntimeException.class,
                () -> ReflectionHelper.setValue(f, instance, 2.123F)
        );
        assertEquals("Unable to set value for field " + fieldName + ".", e.getMessage());
    }

    @Test
    void getAnnotatedFieldsShouldReturnAllFields() {
        final Set<String> annotated = ReflectionHelper
                .getValueAnnotatedFields(TestClass.class)
                .stream()
                .map(Field::getName)
                .collect(Collectors.toSet());
        final Set<String> expected = new HashSet<>(Arrays.asList(
                "publicInt", "publicFinalInt", "publicStaticInt", "publicStaticFinalInt"
        ));
        assertEquals(expected, annotated);
    }

    @Test
    void getConstructor() {
        final Constructor<TestClass> constructor = ReflectionHelper.getDefaultConstructor(TestClass.class);
        assertNotNull(constructor);
    }

    @Test
    void getConstructorShouldThrowWhenNoPublicConstructorAvailable() {
        Exception e = assertThrows(
                NoAccessibleConstructorException.class,
                () -> ReflectionHelper.getDefaultConstructor(NoPublicConstructor.class)
        );
        assertEquals(
                "Provided class must have default constructor and must not be non-static nested class: NoPublicConstructor",
                e.getMessage()
        );
    }

    @Test
    void getConstructorShouldThrowWhenNonStaticInnerClass() {
        Exception e = assertThrows(
                NoAccessibleConstructorException.class,
                () -> ReflectionHelper.getDefaultConstructor(NonStaticInnerClass.class)
        );
        assertEquals(
                "Provided class must have default constructor and must not be non-static nested class: NonStaticInnerClass",
                e.getMessage()
        );
    }

    @Test
    void getConstructorShouldThrowWhenAbstractClass() {
        Exception e = assertThrows(
                AbstractClassInstantiationException.class,
                () -> ReflectionHelper.getDefaultConstructor(AbstractClass.class)
        );
        assertEquals(
                "Provided class must not be interface nor abstract class: AbstractClass",
                e.getMessage()
        );
    }

    @Test
    void getConstructorShouldThrowWhenInterface() {
        Exception e = assertThrows(
                AbstractClassInstantiationException.class,
                () -> ReflectionHelper.getDefaultConstructor(InterfaceClass.class)
        );
        assertEquals(
                "Provided class must not be interface nor abstract class: InterfaceClass",
                e.getMessage()
        );
    }

    @Test
    void getInstance() {
        final TestClass instance = ReflectionHelper.getInstance(TestClass.class);
        assertNotNull(instance);
    }

    @Test
    void getInstanceShouldThrowWhenInvocationException() {
        Exception e = assertThrows(
                RuntimeException.class,
                () -> ReflectionHelper.getInstance(InvocationExceptionClass.class)
        );
        assertEquals(
                "Class cannot be instantiated: InvocationExceptionClass",
                e.getMessage()
        );

    }

    private interface InterfaceClass {

    }

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    private static class TestClass {

        @Value(startByte = 3)
        public static final int publicStaticFinalInt = 123;
        protected static final int protectedStaticFinalInt = 123;
        static final int defaultStaticFinalInt = 123;
        private static final int privateStaticFinalInt = 123;
        @Value(startByte = 2)
        public static int publicStaticInt = 123;
        protected static int protectedStaticInt = 123;
        static int defaultStaticInt = 123;
        private static int privateStaticInt = 123;
        @Value(startByte = 1)
        public final int publicFinalInt = 123;
        protected final int protectedFinalInt = 123;
        final int defaultFinalInt = 123;
        private final int privateFinalInt = 123;
        @Value(startByte = 0)
        public int publicInt = 123;
        protected int protectedInt = 123;
        int defaultInt = 123;
        private int privateInt = 123;


        public TestClass() {
            //empty
        }
    }

    private static class NoPublicConstructor {
        private NoPublicConstructor(@SuppressWarnings("unused") Object o1) {
            //empty
        }
    }

    private static class InvocationExceptionClass {
        public InvocationExceptionClass() {
            throw new RuntimeException();
        }
    }

    private class NonStaticInnerClass {
        public NonStaticInnerClass() {
            //empty
        }
    }

    private abstract class AbstractClass {
        public AbstractClass() {
            //empty
        }
    }
}