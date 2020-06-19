package eu.trisquare.bytemapper.fieldmapper;

public class StandardStructureMapperProvider implements StructureMapperProvider {

    /**
     * Mapper for structures
     */
    public static final StructureMapper structureMapper = new StandardTypeMapper()::toStructure;

    /**
     * {@inheritDoc}
     */
    @Override
    public StructureMapper getStructureMapper(Class<?> clazz) {
        return structureMapper;
    }

}
