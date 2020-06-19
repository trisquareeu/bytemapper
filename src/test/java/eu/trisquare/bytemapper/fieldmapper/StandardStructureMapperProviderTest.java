package eu.trisquare.bytemapper.fieldmapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StandardStructureMapperProviderTest {

    @Test
    void getStructureMapperShouldReturnMapperForObject() {
        final StructureMapper mapper = new StandardStructureMapperProvider().getStructureMapper(Object.class);
        assertNotNull(mapper);
    }

    @Test
    void getStructureMapperShouldReturnMapperForNull() {
        final StructureMapper mapper = new StandardStructureMapperProvider().getStructureMapper(null);
        assertNotNull(mapper);
    }

}
