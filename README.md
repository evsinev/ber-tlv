ber-tlv
=======

BER-TLV parser and builder


How to parse
------------

```
  byte[] bytes = HexUtil.parseHex("50045649534157131000023100000033D44122011003400000481F");

  BerTlvParser parser = new BerTlvParser(LOG);
  BerTlvs tlvs = parser.parse(bytes, 0, bytes.length);
  
  BerTlvLogger.log("    ", tlvs, LOG);
```

How to build
------------

```
byte[] bytes =  new BerTlvBuilder()
                .addHex(new BerTag(0x50), "56495341")
                .addHex(new BerTag(0x57), "1000023100000033D44122011003400000481F")
                .buildArray();
```


## License

The BerTlv framework is licensed under the Apache License 2.0
