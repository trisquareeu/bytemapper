package eu.trisquare.bytemapper.fieldmapper;

public interface StructureMapperProvider {

    /**
     * Returns mapper for structures.
     *
     * @param clazz structure to get mapper for
     * @return {@link StructureMapper} mapper instance
     */
    StructureMapper getStructureMapper(Class<?> clazz);

}
