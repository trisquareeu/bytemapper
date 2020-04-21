# Overview
ByteMapper is a java library helping with deserialization of raw byte data into Plain Old Java Objects (POJO).
It handles object instantiation and mapping values from bytes into annotated fields. 

![Build Status](https://travis-ci.com/trisquareeu/bytemapper.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/trisquareeu/bytemapper/badge.svg?branch=master)](https://coveralls.io/github/trisquareeu/bytemapper?branch=master)
[![javadoc](https://javadoc.io/badge2/eu.trisquare/bytemapper/javadoc.svg)](https://javadoc.io/doc/eu.trisquare/bytemapper)
[![Maven Central](https://img.shields.io/maven-central/v/eu.trisquare/bytemapper.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22eu.trisquare%22%20AND%20a:%22bytemapper%22)

# Get ready
Add following dependency to your projects POM:
```xml
<dependency>git 
    <groupId>eu.trisquare</groupId>
    <artifactId>bytemapper</artifactId>
    <version>1.0-RC1</version>
</dependency> 
```
For other build tools, please refer [here](https://maven-badges.herokuapp.com/maven-central/eu.trisquare/bytemapper). 
You can also download jar files from [here](https://github.com/trisquareeu/bytemapper/releases).

# How to use?
## Example
A picture is worth a thousand words, so let's take a look on this snippet:
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
        //Map fields to update existing object
        DemoObject object2 = new DemoObject();
        ByteMapper.mapValues(object2, buffer);
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
            
        public DemoObject() {
            //instantiated classes must have public default constructor
        }    
    }
}
```

## Supported types
Currently, supported types (and it's wrappers) are: 
* **String** which is created from selected byte range. Size of single character is determined dynamically by UTF-8 standard.
* **BigInteger** that, in theory, can hold up to Integer.MAX_VALUE bytes of data.
* **double** encoded in eight-bytes IEEE 754 floating-point value.
* **float** encoded in four-bytes IEEE 754 floating-point value.
* **Byte[]** is a slice of datasource content as Byte objects array.
* **byte[]** is a slice of datasource content. 
* **long**, which is eight-bytes integer value.
* **int**, which is four-bytes integer value.
* **short**, which is two-bytes integer value.
* **byte** is a one-byte value.
* **boolean** which is logical false if all selected bytes are zeroes, otherwise is true.

Mapper will check if annotated field is assignable by one of above types and then perform conversions from bytes to that
particular type. If given field is assignable by more than one of listed types first one will be used. 

Good example is Object type that can be assigned by virtually any from above list, but String instance will used. 
On the other hand type Number, which is unassignable by String will be assigned with instance of BigInteger instead.

If field is not assignable by any of above types, UnsupportedTypeException will be thrown.

## Endianness
ByteMapper supports both Big- and Little-Endian byte order, although bigEndian is used by default. You can change this,
by setting relevant property of Value annotation: `@Value(startByte = 0, bigEndian=false)`. Results will be as follows:
* 0x0F mapped as little-endian byte will result in 0x0F(=15 decimal) value.
* 0x000F mapped as little-endian short will result in 0x0F00(=3840 decimal) value.
* 0x0000000F mapped as little-endian int will result in 0x0F000000 value
* etc.
In similar way you can apply this rule to String and array types, resulting in reversed byte order.

## Quick note on signedness
Java supports only signed integer types. This is effectively limiting by half maximum value that could be stored in these,
when compared to unsigned types. Java considers negative number when the most significant bit (MSB) is set to 1. <br/>
You still can choose if you want to treat your input as a signed or unsigned value, thought. When length of provided data input will 
exactly match chosen type size (i.e. four bytes for type of int), value will be considered signed (first, most significant 
bit will directly depend on inputs content). You can however use bigger type than incoming data (i.e. type of long for the same, 
four-bytes value) that will eventually make its most-significant bit nonassignable by input data, resulting in always-positive
(unsigned) value.

**Example**:<br>
* Your data contains byte 0xFF. It's equal to -1 in two's complement signed representation and 255 when considered unsigned.
You can choose to get signed value, by mapping it as a Java's byte, which size will exactly match inputs size. MSB will be set
to 1, resulting in negative value. On the other hand, you can use short or any bigger data type to map this byte. In this situation,
most significant byte of Java's type will be always 0x00, resulting in positive value, which is equal to 255.
* Your data contains short 0xFFFF. Similar to previous, it may be -1 when using two's complement signed value representation
or 65535. If you map it as a short, you'll get signed (-1) value. If you use bigger data type, it will be stored as a unsigned
65535 value.
* Everything above applies to Java's integer data types for which bigger type is present. If you want to map unsigned long value
into Java's object, you have to use BigInteger. You'll get signed value, however. It may be converted to unsigned one by calling
`BigInteger unsignedBigInteger = new BigInteger(1, signedBigInteger.toByteArray())`. You can find example for this in test scenarios.

# Licence 
This project is under permissive, MIT licence. Please refer to LICENSE file for more details.

# Contribute!
Any support to this project will be warmly welcomed. You can contribute either by issuing bug report, 
feature request or by the most appreciated way, creating a pull request. 

This project is in its early stage of development, currently maintained by only one person as a helper 
tool for private projects. It may (and probably does) contain bugs, even if most of the use cases are 
covered by provided tests. 

If you found any bug or invented improvement, don't hesitate to let me know about it!
