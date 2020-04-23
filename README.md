# Overview
ByteMapper is a java library helping with deserialization of raw byte data into Plain Old Java Objects (POJO).
It handles object instantiation and mapping values from bytes into annotated fields. 

[![Build Status](https://travis-ci.com/trisquareeu/bytemapper.svg?branch=master)](https://travis-ci.com/trisquareeu/bytemapper)
[![Coverage Status](https://coveralls.io/repos/github/trisquareeu/bytemapper/badge.svg?branch=master)](https://coveralls.io/github/trisquareeu/bytemapper?branch=master)
[![javadoc](https://javadoc.io/badge2/eu.trisquare/bytemapper/javadoc.svg)](https://javadoc.io/doc/eu.trisquare/bytemapper)
[![Maven Central](https://img.shields.io/maven-central/v/eu.trisquare/bytemapper.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22eu.trisquare%22%20AND%20a:%22bytemapper%22)

# Get ready
Add following dependency to your projects POM:
```xml
<dependency> 
    <groupId>eu.trisquare</groupId>
    <artifactId>bytemapper</artifactId>
    <version>1.0-RC3</version>
</dependency> 
```
For other build tools, please refer [here](https://maven-badges.herokuapp.com/maven-central/eu.trisquare/bytemapper). 
You can also download jar files from [here](https://github.com/trisquareeu/bytemapper/releases).

# How to use?
## Example
A picture is worth a thousand words, so let's take a look on these two snippets:
### Annotated fields
```java
public class Main {
    
    public static void main(String[] args){
        ByteBuffer buffer = ByteBuffer
                .allocate(16)
                .put((byte) 0x00)                               //bytes 0 to 1
                .put((byte) 0xFF)                               //bytes 1 to 2
                .putShort(Short.MAX_VALUE)                      //bytes 2 to 4
                .putInt(Integer.MAX_VALUE)                      //bytes 4 to 8
                .putLong(Long.MAX_VALUE)                        //bytes 8 to 16
                .flip();
        //Instantiate object from ByteBuffer content
        DemoObject object = ByteMapper.mapValues(DemoObject.class, buffer);
    }

    //inner classes must be declared static to be instantiated
    private static class DemoObject {   
        /** Value mapped from byte 0 to 1. */
        @Value(startByte = 0)
        private boolean booleanValue;

        /** Value mapped from byte 1 to 2 */
        @Value(startByte = 1)
        private byte byteValue;

        /** Value mapped from byte 2 to 4 */
        @Value(startByte = 2, size = 2)
        private short shortValue;

        /** Value mapped from byte 4 to 8 */
        @Value(startByte = 4, size = 4)
        private int intValue;

        /** Value mapped from byte 8 to 16 */
        @Value(startByte = 8, size = 8)
        private long longValue;
            
        private DemoObject() {
            //instantiated classes must have default constructor
        }    
    }
}
```
All fields annotated by `@Value` will be assigned with mapped values. These fields must not be `final` nor `static`. You are obliged to provide accessible,
default (no-argument) constructor to allow ByteMapper library to create a new instance of given class. 

### Annotated constructor
```java
public class Main {
    
    public static void main(String[] args){
        ByteBuffer buffer = ByteBuffer
                .allocate(16)
                .put((byte) 0x00)                               //bytes 0 to 1
                .put((byte) 0xFF)                               //bytes 1 to 2
                .putShort(Short.MAX_VALUE)                      //bytes 2 to 4
                .putInt(Integer.MAX_VALUE)                      //bytes 4 to 8
                .putLong(Long.MAX_VALUE)                        //bytes 8 to 16
                .flip();
        //Instantiate object from ByteBuffer content
        DemoObject object = ByteMapper.mapValues(DemoObject.class, buffer);
    }

    //inner classes must be declared static to be instantiated
    private static class DemoObject {   
        /** Value mapped from byte 0 to 1. */
        private final boolean booleanValue;

        /** Value mapped from byte 1 to 2 */
        private final byte byteValue;

        /** Value mapped from byte 2 to 4 */
        private final short shortValue;

        /** Value mapped from byte 4 to 8 */
        private final int intValue;

        /** Value mapped from byte 8 to 16 */
        private final long longValue;
            
        @ByteMapperConstructor
        private DemoObject(
                @Value(startByte = 0) boolean booleanValue,
                @Value(startByte = 1) byte byteValue,
                @Value(startByte = 2, size = 2) short shortValue, 
                @Value(startByte = 4, size = 4) int intValue,
                @Value(startByte = 8, size = 8) long longValue
        ) {
            this.booleanValue = booleanValue;
            this.byteValue = byteValue;
            this.shortValue = shortValue;
            this.intValue = intValue;
            this.longValue = longValue;
        }    
    }
}
```
If you decide to use annotated constructor, all of its parameters must be annotated by `@Value`. Only one annotated constructor may exists in given
class, and it's supertypes. If annotated constructor is present, annotations on class fields will be ignored. Otherwise, if no annotated constructor is present, 
ByteMapper library will try to instantiate class using default (no-argument) constructor and only then will process fields annotations. 

## Supported types
Currently, supported types (and it's wrappers) are: 
* **String** - You can map up to Integer.MAX_VALUE bytes of data into a String. Size of single character is determined dynamically by UTF-8 standard. Bytes may be processed in direct or reversed order. See chapter about endianness to get more on this.
* **BigInteger** - You can map up to Integer.MAX_VALUE bytes of data into a BigInteger. Created instance holds a signed value, but conversion to unsigned is possible. See below note on the signedness for details.
* **double** - You can map up to eight bytes of IEEE 754 floating-point to a double.
* **float** - You can map up to four bytes of IEEE 754 floating-point to a float.
* **Byte[]** - You can map up to Integer.MAX_VALUE bytes of data into an object Byte array. Result is effectively slice of input data in direct or reversed order. See the chapter about endianness to get more on this.
* **byte[]** - You can map up to Integer.MAX_VALUE bytes of data into a primitive byte array. Result is effectively slice of input data in direct or reversed order. See the chapter about endianness to get more on this.
* **long** - You can map up to eight bytes of data into a long. Created type is signed. See below note on the signedness if you want to map unsigned value (conclusion: use the BigInteger for unsigned, eight-bytes value and then make in non-negative).
* **int** - You can map up to four bytes of data into an integer. Created type is signed. See below note on the signedness if you want to map unsigned value (conclusion: use the long for unsigned, four-bytes value, and it will never be negative).
* **short** - You can map up to two bytes of data into a short. Created type is signed. See below note on the signedness if you want to map unsigned value (conclusion: use the int or the long for unsigned, two-bytes value, and it will never be negative).
* **byte** - You can map up to one byte of data into byte. Created type is signed. See below note on the signedness if you want to map unsigned value (conclusion: use the short, the int or the long for unsigned, one-byte value, and it will never be negative).
* **boolean** - You can map up to Integer.MAX_VALUE bytes of data into a boolean. Resulting value will be logical false if all scoped bytes are zeroes, otherwise will be true. This type will ignore the signedness as well as the endianness as not applicable.

Mapper will check if annotated field is assignable by one of above types and then perform conversions from bytes to that
particular type. If given field is assignable by more than one of listed types, first one will be used. 

Good example is Object type that can be assigned by virtually any from above list, but String instance will be used. 
On the other hand type Number, which is nonassignable by String will be assigned with instance of BigInteger instead.

If field is not assignable by any of above types, UnsupportedTypeException will be thrown.

## Endianness
ByteMapper supports both big- and little-endian byte order, although big-endian is used by default. You can change this,
by setting relevant property of Value annotation: `@Value(startByte = 0, bigEndian=false)`. Results will be as follows:
* 0x0F mapped as little-endian byte will result in 0x0F(=15 decimal) value.
* 0x000F mapped as little-endian short will result in 0x0F00(=3840 decimal) value.
* 0x0000000F mapped as little-endian int will result in 0x0F000000 value
* etc.
In similar way you can apply this rule to String and array types, resulting in reversed byte order.

## Quick note on signedness
Java supports only signed integer types. Maximum value is effectively limited by half, 
when compared to unsigned types. Java considers a negative number when the most significant bit (MSB) is set to 1. <br/>
You still can choose if you want to treat your input as a signed or unsigned value, though. When the length of provided data input will 
exactly match the chosen type size (i.e. one-byte value for the type of byte), value will be considered signed (first, most significant 
bit will directly depend on input content). You can however use bigger type than incoming data (i.e. the type of short which uses two bytes for the same, 
one-byte value) that will eventually make its most-significant bit unreachable by any bits of input data (padding zeroes will be put to align sizes), 
resulting in always-positive (unsigned) value. This design ensures that mapped value can be always stored in annotated field data type.

**Example**:<br>
* Your data contains byte 0xFF. It's equal to -1 in two's complement signed representation and 255 when considered unsigned.
You can choose to get signed value, by mapping it as a Java's byte, which size will exactly match input size. MSB will be set
to 1, resulting in negative values. On the other hand, you can use short or any bigger data type to map this byte. In this situation,
most significant byte of Java's type will be always 0x00, resulting in positive value, which is equal to 255.
* Your data contains short 0xFFFF. Similar to previous, it may be -1 when using two's complement signed value representation
or 65535. If you map it as a short, you'll get signed (-1) value. If you use bigger data type, it will be stored as an unsigned
65535 value.
* Everything above applies to Java's integer data types for which bigger type is present. If you want to map unsigned long value
into Java's object, you have to use BigInteger. You'll get signed value, however. It may be converted to unsigned one by calling
`BigInteger unsignedBigInteger = new BigInteger(1, signedBigInteger.toByteArray())`. You can find an example for this in test scenarios.

# Licence 
This project is under permissive, MIT license. Please refer to LICENSE file for more details.

# Contribute!
Any support for this project will be warmly welcomed. You can contribute either by issuing bug report, 
feature request or by the most appreciated way, creating a pull request. 

This project is in its early stage of development, currently maintained by only one person as a helper 
tool for private projects. It may (and probably does) contain bugs, even if most of the use cases are 
covered by provided tests. 

If you found any bug or invented improvement, don't hesitate to let me know about it!
