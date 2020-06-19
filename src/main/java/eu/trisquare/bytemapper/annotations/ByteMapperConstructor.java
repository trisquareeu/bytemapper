package eu.trisquare.bytemapper.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;

/**
 * Used to mark constructor which should be used by ByteMapper to instantiate class.
 * Class can contain only one constructor annotated with this interface, this restriction
 * includes all superclasses.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({CONSTRUCTOR})
public @interface ByteMapperConstructor {

}
