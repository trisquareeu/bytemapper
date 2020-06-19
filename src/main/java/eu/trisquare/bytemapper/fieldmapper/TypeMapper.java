package eu.trisquare.bytemapper.fieldmapper;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Methods used to convert ByteBuffer content into objects' instances
 */
interface TypeMapper {

    /**
     * Converts {@code size} bytes of data, starting from {@code startByte} into boolean.
     * Conversion is based on each scoped byte content - if all are equal to zero, resulting
     * boolean will be false. If any of scoped bytes holds non-zero value, method will return
     * true.
     *
     * @param buffer      source of data to determine outcome
     * @param isBigEndian does not affect method outcome
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        number of bytes to scope
     * @return false, if each byte in {@code buffer} limited by {@code startByte} and
     * {@code startByte+size} is zero, otherwise true
     */
    boolean toBoolean(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Returns singe signed byte at {@code startByte} position in {@code buffer} source.
     *
     * @param buffer      source of data to get byte from
     * @param isBigEndian does not affect method outcome
     * @param startByte   position (0-inclusive index) of returned byte
     * @param size        must be always 1
     * @return byte on {@code startByte} position
     */
    byte toByte(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Returns two-bytes long signed value from {@code buffer} source, starting from {@code startByte}
     * position and ending on {@code startByte+size}. If {@code isBigEndian} flag is set to false,
     * returned value will have reversed byte order: from {@code startByte+size} to {@code startByte}.
     *
     * @param buffer      source of data to get data from
     * @param isBigEndian determines data endianness. If set to {@code false}, bytes will be reversed before conversion
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        must be bigger than 0 and not exceed 2
     * @return short constructed from {@code buffer} content limited by {@code startByte} and {@code startByte+size}
     */
    short toShort(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Returns four-bytes long signed value from {@code buffer} source, starting from {@code startByte}
     * position and ending on {@code startByte+size}. If {@code isBigEndian} flag is set to false,
     * returned value will have reversed byte order: from {@code startByte+size} to {@code startByte}.
     *
     * @param buffer      source of data to get data from
     * @param isBigEndian determines data endianness. If set to {@code false}, bytes will be reversed before conversion
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        must be bigger than 0 and not exceed 4
     * @return int constructed from {@code buffer} content limited by {@code startByte} and {@code startByte+size}
     */
    int toInt(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Returns eight-bytes long signed value from {@code buffer} source, starting from {@code startByte}
     * position and ending on {@code startByte+size}. If {@code isBigEndian} flag is set to false,
     * returned value will have reversed byte order: from {@code startByte+size} to {@code startByte}.
     *
     * @param buffer      source of data to get data from
     * @param isBigEndian determines data endianness. If set to {@code false}, bytes will be reversed before conversion
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        must be bigger than 0 and not exceed 8
     * @return int constructed from {@code buffer} content limited by {@code startByte} and {@code startByte+size}
     */
    long toLong(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Returns eight-bytes long IEEE754 floating-point value from {@code buffer} source, starting from {@code startByte}
     * position and ending on {@code startByte+size}. If {@code isBigEndian} flag is set to false,
     * returned value will have reversed byte order: from {@code startByte+size} to {@code startByte}.
     *
     * @param buffer      source of data to get data from
     * @param isBigEndian determines data endianness. If set to {@code false}, bytes will be reversed before conversion
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        must be bigger than 0 and not exceed 8.
     * @return double constructed from {@code buffer} content limited by {@code startByte} and {@code startByte+size}
     */
    double toDouble(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Returns four-bytes long IEEE754 floating-point value from {@code buffer} source, starting from {@code startByte}
     * position and ending on {@code startByte+size}. If {@code isBigEndian} flag is set to false,
     * returned value will have reversed byte order: from {@code startByte+size} to {@code startByte}.
     *
     * @param buffer      source of data to get data from
     * @param isBigEndian determines data endianness. If set to {@code false}, bytes will be reversed before conversion
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        must be bigger than 0 and not exceed 4.
     * @return float constructed from {@code buffer} content limited by {@code startByte} and {@code startByte+size}
     */
    float toFloat(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Returns {@code size} bytes from {@code buffer} starting at {@code startByte} converted to
     * {@link BigInteger} instance, using two's complement notation. If {@code isBigEndian} is set
     * to false, bytes will be obtained in reverse order: from {@code startByte+size} to {@code startByte}.
     *
     * @param buffer      source of data to get data from
     * @param isBigEndian determines data endianness. If set to {@code false}, bytes will be reversed before conversion
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        must be bigger than 0 and not exceed {@link Integer#MAX_VALUE}
     * @return BigInteger constructed form {@code buffer} content limited by {@code startByte} and {@code startByte+size}
     */
    BigInteger toBigInteger(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Returns {@code size} bytes from {@code buffer} converted to String, using {@code UTF-8} character encoding.
     * If {@code isBigEndian} is set to false, bytes will be obtained in reverse order: from {@code startByte+size}
     * to {@code startByte}.
     *
     * @param buffer      source of data to get data from
     * @param isBigEndian determines data endianness. If set to {@code false}, bytes will be reversed before conversion
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        must be bigger than 0 and not exceed {@link Integer#MAX_VALUE}
     * @return UTF-8 encoded String from bytes in provided range
     */
    String toString(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Gets {@code size} bytes from {@code source}, starting from {@code startByte} and ending on
     * {@code startByte+size} as a primitive bytes array. If {@code isBigEndian} flag is set to
     * {@code false}, returned array will have opposite order than data in source (will be copied
     * from {@code startByte+size} to {startByte}).
     *
     * @param buffer      source of data to get data from
     * @param isBigEndian determines data endianness. If set to {@code false}, bytes order will be reversed.
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        amount of bytes to copy from {@code buffer}
     * @return Primitive bytes array containing {@code size} bytes copied form {@code buffer}
     * content limited by {@code startByte} and {@code startByte+size}
     */
    byte[] toByteArray(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    /**
     * Gets {@code size} bytes from {@code source}, starting from {@code startByte} and ending on
     * {@code startByte+size} as a object Bytes array. If {@code isBigEndian} flag is set to
     * {@code false}, returned array will have opposite order than data in source (will be copied
     * from {@code startByte+size} to {startByte}).
     *
     * @param buffer      source of data to get data from
     * @param isBigEndian determines data endianness. If set to {@code false}, bytes order will be reversed.
     * @param startByte   position (0-inclusive index) of first scoped byte
     * @param size        amount of bytes to copy from {@code buffer}
     * @return Object Bytes array containing {@code size} bytes copied form {@code buffer}
     * content limited by {@code startByte} and {@code startByte+size}
     */
    Byte[] toByteObjectArray(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);


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
    <T> T toStructure(ByteBuffer buffer, Class<T> structureType, int startByte, int size);
}
