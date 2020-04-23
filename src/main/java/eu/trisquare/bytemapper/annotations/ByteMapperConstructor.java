package eu.trisquare.bytemapper.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;

@Retention(RetentionPolicy.RUNTIME)
@Target({CONSTRUCTOR})
public @interface ByteMapperConstructor {

}
