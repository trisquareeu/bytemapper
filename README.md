# Overview
ByteMapper is a java library helping with deserialization of raw byte data into Plain Old Java Objects (POJO).
It handles object instantiation and mapping values from bytes into annotated fields. 

![Build Status](https://travis-ci.com/trisquareeu/bytemapper.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/trisquareeu/bytemapper/badge.svg)](https://coveralls.io/github/trisquareeu/bytemapper)
[![javadoc](https://javadoc.io/badge2/eu.trisquare/bytemapper/javadoc.svg)](https://javadoc.io/doc/eu.trisquare/bytemapper)
[![Maven Central](https://img.shields.io/maven-central/v/eu.trisquare/bytemapper.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22eu.trisquare%22%20AND%20a:%22bytemapper%22)

# Get ready
Add following dependency to your projects POM:
```xml
<dependency>
    <groupId>eu.trisquare</groupId>
    <artifactId>bytemapper</artifactId>
    <version>0.1-ALPHA</version>
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
* **BigInteger** that can hold up to Integer.MAX_VALUE bytes of data.
* **Byte[]** returns slice of datasource content as Byte objects array. This data type is **not** converted into unsigned values array. 
* **byte[]** returns slice of datasource content. This data type is **not** converted into unsigned values array. 
* **long**, which is mapped as big- or little-endian eight bytes unsigned value.  See disclaimer about signedness below.
* **int**, which is mapped as big- or little-endian four bytes unsigned value. See disclaimer about signedness below.
* **short**, which is mapped as big- or little-endian two bytes unsigned value. See disclaimer about signedness below.
* **byte** is assigned with same value as in data source. This data type is **not** converted into unsigned value. 
* **boolean** which is mapped to false if all selected bytes are zeroes, otherwise is mapped to true.

Mapper will check if annotated field is assignable by one of above types and then perform conversions from bytes to that
particular type. If given field is assignable by more than one of given types (good example is Object type that can be 
assigned by virtually any from above list), first one will be used. For mentioned type Object, **String** instance will
be returned, but for type Number, **BigInteger** will be used instead. If field is not assignable by any of above types,
NoMapperFoundException will be thrown.

## Quick note on signedness
Currently, this library supports only **unsigned** values for types other than **byte, byte[] and Byte[]**, which are
stored as signed values. That's shorten every other data type value range to only non-negative half.

**Example**:<br>
Even if maximum size allowed for *short* type is two bytes,  maximum stored value is equal to  Short.MAX_VALUE (0x7FFF).
Bigger values like 0xFFFF will exceed it, resulting in ArithmeticException. Exception to this rule are **byte, byte[] and Byte[]** 
which are taken as-is from source buffer to allow developer handle signed values.

# Licence 
This project is under permissive, MIT licence. Please refer to LICENSE file for more details.

# Contribute!
Any support to this project will be warmly welcomed. You can contribute either by issuing bug report, 
feature request or by the most appreciated way, creating a pull request. 

This project is in its early stage of development, currently maintained by only one person as a helper 
tool for private projects. It may (and probably does) contain bugs, even if most of the use cases are 
covered by provided tests. 

If you found any bug or invented improvement, don't hesitate to let me know about it!
