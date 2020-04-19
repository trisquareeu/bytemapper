package eu.trisquare.bytemapper.fieldmapper;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provides FieldMapper eligible for current processed field
 */
public class FieldMapperProvider {

    /**
     * List of known mappers stored in importance order
     */
    private static final List<FieldMapper> mappers;

    static {
        mappers = new ArrayList<>();

        /* Default mapper for types assignable from String class (i.e. String, Object or CharSequence) */
        mappers.add(new ArrayFieldMapper(FieldMapperHelper::toString, String.class));

        /* Default mapper for types assignable from Number class */
        mappers.add(new SingleValueFieldMapper(FieldMapperHelper::toBigInteger, Integer.MAX_VALUE, BigInteger.class));

        /* Default mapper for types assignable from Byte[] class (i.e. Byte[], Object[] and Object) */
        mappers.add(new ArrayFieldMapper(FieldMapperHelper::toByteObjectArray, Byte[].class));

        /* Default mapper for types assignable from byte[] class (i.e. byte[] and Object) */
        mappers.add(new ArrayFieldMapper(bytes -> bytes, byte[].class));

        /* Default mapper for types assignable from long class (i.e. long, Long, Number and Object) */
        mappers.add(new SingleValueFieldMapper(FieldMapperHelper::toLong, Long.BYTES, long.class));

        /* Default mapper for types assignable from int class (i.e. int, Integer, Number and Object) */
        mappers.add(new SingleValueFieldMapper(FieldMapperHelper::toInt, Integer.BYTES, int.class));

        /* Default mapper for types assignable from short class (i.e. short, Short, Number and Object) */
        mappers.add(new SingleValueFieldMapper(FieldMapperHelper::toShort, Short.BYTES, short.class));

        /* Default mapper for types assignable from byte class (i.e. byte, Byte, Number and Object) */
        mappers.add(new SingleValueFieldMapper(FieldMapperHelper::toByte, Byte.BYTES, byte.class));

        /* Default mapper for types assignable from boolean class (i.e. boolean, Boolean and Object) */
        mappers.add(new SingleValueFieldMapper(FieldMapperHelper::toBoolean, Byte.BYTES, boolean.class));
    }

    /**
     * Returns first eligible mapper for given field type.
     *
     * @param clazz of field to fill with mapped value
     * @return FieldMapper eligible for given data type provisioning
     * @throws NoMapperFoundException if no mapper was found for given data type.
     */
    public static FieldMapper getMapper(Class<?> clazz) {
        final Optional<FieldMapper> oMapper = mappers.stream()
                .filter(mapper -> mapper.isEligible(clazz))
                .findFirst();

        if (!oMapper.isPresent()) {
            throw new NoMapperFoundException(clazz);
        }
        return oMapper.get();
    }
}
