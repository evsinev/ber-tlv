[![maven](https://maven-badges.herokuapp.com/maven-central/com.payneteasy/ber-tlv/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.payneteasy/ber-tlv)
[![Build Status](https://travis-ci.org/evsinev/ber-tlv.svg?branch=master)](https://travis-ci.org/evsinev/ber-tlv)

BER-TLV parser and builder
==========================

BerTlv is a java library for parsing and building BER TLV encoded data.

## Features

* supported types: amount, date, time, text, BCD, bytes
* thread safe (provides immutable container BerTlv)
* production ready (uses in several projects)
* lightweight (no external dependencies)

## Setup with dependency managers

### Maven

```xml
<dependency>
  <groupId>com.payneteasy</groupId>
  <artifactId>ber-tlv</artifactId>
  <version>$VERSION</version>
</dependency>
```

### Gradle

```groovy
compile 'com.payneteasy:ber-tlv:$VERSION'
```

How to parse
------------

```java
byte[] bytes = HexUtil.parseHex("50045649534157131000023100000033D44122011003400000481F");

BerTlvParser parser = new BerTlvParser(LOG);
BerTlvs tlvs = parser.parse(bytes, 0, bytes.length);
  
BerTlvLogger.log("    ", tlvs, LOG);
```

How to build
------------

```java
byte[] bytes =  new BerTlvBuilder()
                .addHex(new BerTag(0x50), "56495341")
                .addHex(new BerTag(0x57), "1000023100000033D44122011003400000481F")
                .buildArray();
```


## License

The BerTlv framework is licensed under the Apache License 2.0
