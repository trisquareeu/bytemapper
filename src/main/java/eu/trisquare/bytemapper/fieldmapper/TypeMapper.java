package eu.trisquare.bytemapper.fieldmapper;

import java.math.BigInteger;
import java.nio.ByteBuffer;

interface TypeMapper {

    boolean toBoolean(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    byte toByte(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    short toShort(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    int toInt(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    long toLong(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    double toDouble(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    float toFloat(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    BigInteger toBigInteger(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    String toString(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    byte[] toByteArray(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);

    Byte[] toByteObjectArray(ByteBuffer buffer, boolean isBigEndian, int startByte, int size);
}
