package eu.trisquare.bytemapper.fieldmapper;

import java.nio.ByteBuffer;

/**
 * Interface for mappers obtaining values from ByteBuffer input to actual POJO structures' instances
 */
public interface StructureMapper {

    /**
     * Gets {@code size} bytes from {@code source}, starting from {@code startByte} and ending on
     * {@code startByte+size} then converts it to POJO structure.
     *
     * @param buffer        source of data to get data from
     * @param structureType proccesed structure POJO class
     * @param startByte     position (0-inclusive index) of first scoped byte
     * @param size          amount of bytes to copy from {@code buffer}
     * @param <T>           type of processed structure
     * @return POJO class of type T created from bytebuffer's data slice
     */
    <T> T getValue(ByteBuffer buffer, Class<T> structureType, int startByte, int size);

}
