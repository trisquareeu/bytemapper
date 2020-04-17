# Overview
ByteMapper is a java library helping with deserialization of raw byte data into Plain Old Java Objects (POJO).
It handles object instantiation and mapping values from bytes into annotated fields. 

![Build Status](https://travis-ci.com/trisquareeu/bytemapper.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/trisquareeu/bytemapper/badge.svg)](https://coveralls.io/github/trisquareeu/bytemapper)
[![javadoc](https://javadoc.io/badge2/eu.trisquare/bytemapper/javadoc.svg)](https://javadoc.io/doc/eu.trisquare/bytemapper)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.trisquare/bytemapper/badge.svg)](https://maven-badges.herokuapp.com/maven-central/eu.trisquare/bytemapper)

# Get ready
Add following dependency to your projects POM:
```xml
<dependency>
    <groupId>eu.trisquare</groupId>
    <artifactId>bytemapper</artifactId>
    <version>0.1-ALPHA</version>
</dependency> 
```

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
* **boolean** which is mapped to false if all selected bytes are zeroes, otherwise is mapped to true.
* **byte** is assigned with same value as in data source. As only one data type is not converted into unsigned byte. 
* **short**, which is mapped as big- or little-endian two bytes unsigned value. See disclaimer about signedness below.
* **int**, which is mapped as big- or little-endian four bytes unsigned value. See disclaimer about signedness below.
* **long**, which is mapped as big- or little-endian eight bytes unsigned value.  See disclaimer about signedness below.
* **String** which is created from selected byte range. Size of single character is determined by UTF-8 standard.
* **byte[]** returns slice of datasource content.
* **Byte[]** returns slice of datasource content as Byte objects array.

## Quick note on signedness
Currently, this library supports only **unsigned** values. Even if maximum size allowed for **short** type is two bytes, 
please keep in mind that maximum supported value is equal to 0x7FFF(=Short.MAX_VALUE) and values like 0xFFFF will exceed it. 
Please consider this when choosing type for fields to avoid ArithmeticException. 

# Licence 
This project is under permissive, MIT licence. Please refer to LICENSE file for more details.

# Contribute!
Any support to this project will be warmly welcomed. You can contribute either by issuing bug report, 
feature request or by the most appreciated way, creating a pull request. 

This project is in its early stage of development, currently maintained by only one person as a helper 
tool for private projects. It may (and probably does) contain bugs, even if most of the use cases are 
covered by provided tests. 

If you found any bug or invented improvement, don't hesitate to let me know about it!
