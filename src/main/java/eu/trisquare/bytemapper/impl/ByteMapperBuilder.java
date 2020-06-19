package eu.trisquare.bytemapper.impl;

import eu.trisquare.bytemapper.ByteMapper;
import eu.trisquare.bytemapper.classmapper.POJOAccessor;
import eu.trisquare.bytemapper.classmapper.StandardByteMapper;
import eu.trisquare.bytemapper.classmapper.StandardPOJOAccessor;
import eu.trisquare.bytemapper.fieldmapper.FieldMapperProvider;
import eu.trisquare.bytemapper.fieldmapper.StandardFieldMapperProvider;
import eu.trisquare.bytemapper.fieldmapper.StandardStructureMapperProvider;
import eu.trisquare.bytemapper.fieldmapper.StructureMapperProvider;

/**
 * Creates and parametrizes ByteBuffer implementation.
 * Use this for behavior customisation if needed.
 */
public class ByteMapperBuilder {

    /**
     * Used to obtain bytes-to-object {@link eu.trisquare.bytemapper.fieldmapper.FieldMapper} instance.
     */
    private FieldMapperProvider fieldMapperProvider;

    /**
     * Used to obtain bytes-to-object {@link eu.trisquare.bytemapper.fieldmapper.StructureMapper} instance.
     */
    private StructureMapperProvider structureMapperProvider;

    /**
     * Used to instantiate classes and assign their fields with mapped values.
     */
    private POJOAccessor pojoAccessor;

    public ByteMapperBuilder() {
        this.fieldMapperProvider = new StandardFieldMapperProvider();
        this.structureMapperProvider = new StandardStructureMapperProvider();
        this.pojoAccessor = new StandardPOJOAccessor();
    }

    public ByteMapperBuilder withFieldMapperProvider(FieldMapperProvider fieldMapperProvider) {
        this.fieldMapperProvider = fieldMapperProvider;
        return this;
    }

    public ByteMapperBuilder withStructureMapperProvider(StructureMapperProvider structureMapperProvider) {
        this.structureMapperProvider = structureMapperProvider;
        return this;
    }

    public ByteMapperBuilder withPOJOAccessor(POJOAccessor pojoAccessor) {
        this.pojoAccessor = pojoAccessor;
        return this;
    }

    /**
     * Creates new instance of ByteMapper using previously provided implementations and parameters.
     *
     * @return new instance of {@link ByteMapper} implementation
     */
    public ByteMapper build() {
        return new StandardByteMapper(fieldMapperProvider, structureMapperProvider, pojoAccessor);
    }

}
