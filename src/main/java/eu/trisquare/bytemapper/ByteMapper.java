package eu.trisquare.bytemapper;

import java.nio.ByteBuffer;


/**
 * helps with deserialization raw bytes data into Plain Old Java Objects (POJO).
 */
public interface ByteMapper {

    /**
     * Handles object instantiation and mapping values from bytes into annotated fields or constructor parameters.
     *
     * @param clazz      to instantiate with one public constructor annotated with {@link eu.trisquare.bytemapper.annotations.ByteMapperConstructor}
     *                   or with accessible default (non-argument) constructor and fields annotated with {@link eu.trisquare.bytemapper.annotations.Value}
     * @param byteBuffer containing data to deserialize into values
     * @param <T>        type of instantiated object
     * @return new instance of object, created from byte buffer content
     */
    <T> T mapValues(Class<T> clazz, ByteBuffer byteBuffer);

}
