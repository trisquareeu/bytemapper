package eu.trisquare.bytemapper.fieldmapper;

public interface FieldMapperProvider {

    /**
     * Returns first eligible mapper for given field type.
     *
     * @param clazz of field to fill with mapped value
     * @return FieldMapper eligible for given data type provisioning
     */
    FieldMapper getMapper(Class<?> clazz);

}
