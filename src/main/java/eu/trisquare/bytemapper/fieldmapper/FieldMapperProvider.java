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

    /**
     * Maps ByteBuffer to particular type
     */
    private static final TypeMapper typeMapper = new StandardTypeMapper();

    static {
        mappers = new ArrayList<>();

        /* Default mapper for types assignable from String class (i.e. String, Object or CharSequence) */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toString,
                Integer.MAX_VALUE,
                String.class
        ));

        /* Default mapper for types assignable from Number class */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toBigInteger,
                Integer.MAX_VALUE,
                BigInteger.class
        ));

        /* Default mapper for types assignable from double class */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toDouble,
                Double.BYTES,
                double.class
        ));

        /* Default mapper for types assignable from float class */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toFloat,
                Float.BYTES,
                float.class
        ));

        /* Default mapper for types assignable from Byte[] class (i.e. Byte[], Object[] and Object) */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toByteObjectArray,
                Integer.MAX_VALUE,
                Byte[].class
        ));

        /* Default mapper for types assignable from byte[] class (i.e. byte[] and Object) */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toByteArray,
                Integer.MAX_VALUE,
                byte[].class
        ));

        /* Default mapper for types assignable from long class (i.e. long, Long, Number and Object) */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toLong,
                Long.BYTES,
                long.class
        ));

        /* Default mapper for types assignable from int class (i.e. int, Integer, Number and Object) */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toInt,
                Integer.BYTES,
                int.class
        ));

        /* Default mapper for types assignable from short class (i.e. short, Short, Number and Object) */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toShort,
                Short.BYTES,
                short.class
        ));

        /* Default mapper for types assignable from byte class (i.e. byte, Byte, Number and Object) */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toByte,
                Byte.BYTES,
                byte.class
        ));

        /* Default mapper for types assignable from boolean class (i.e. boolean, Boolean and Object) */
        mappers.add(new SingleValueFieldMapper(
                typeMapper::toBoolean,
                Integer.MAX_VALUE,
                boolean.class
        ));
    }

    /**
     * Returns first eligible mapper for given field type.
     *
     * @param clazz of field to fill with mapped value
     * @return FieldMapper eligible for given data type provisioning
     * @throws UnsupportedTypeException if no mapper was found for given data type.
     */
    public FieldMapper getMapper(Class<?> clazz) {
        final Optional<FieldMapper> oMapper = mappers.stream()
                .filter(mapper -> mapper.isEligible(clazz))
                .findFirst();

        if (!oMapper.isPresent()) {
            throw new UnsupportedTypeException(clazz);
        }
        return oMapper.get();
    }
}
