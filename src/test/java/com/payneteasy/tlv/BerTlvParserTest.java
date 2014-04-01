package com.payneteasy.tlv;

import org.junit.Test;

public class BerTlvParserTest {

    private static final BerTlvLoggerSlf4j LOG = new BerTlvLoggerSlf4j();

    @Test
    public void testParse() {
        String hex = "50 04 56 49 53 41 57 13 10 00 02 31 00 00 00 33 d4 41 22 01 10 03 40 00 00 48 1f 5a 08 10 00 02 31 00 00 00 33 5f 20 1a 43 41 52 44 33 2f 45 4d 56 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 5f 24 03 44 12 31 5f 28 02 06 43 5f 2a 02 06 43 5f 30 02 02 01 5f 34 01 06 82 02 5c 00 84 07 a0 00 00 00 03 10 10 95 05 40 80 00 80 00 9a 03 14 02 10 9b 02 e8 00 9c 01 00 9f 02 06 00 00 00 03 01 04 9f 03 06 00 00 00 00 00 00 9f 06 07 a0 00 00 00 03 10 10 9f 09 02 00 8c 9f 10 07 06 01 0a 03 a0 a1 00 9f 1a 02 08 26 9f 1c 08 30 36 30 34 35 33 39 30 9f 1e 08 30 36 30 34 35 33 39 30 9f 26 08 cb fc 76 79 77 11 1f 15 9f 27 01 80 9f 33 03 e0 b8 c8 9f 34 03 5e 03 00 9f 35 01 22 9f 36 02 00 0e 9f 37 04 46 1b da 7c 9f 41 04 00 00 00 63";
        byte[] bytes = HexUtil.parseHex(hex);

        BerTlvParser parser = new BerTlvParser(LOG);
        BerTlvs tlvs = parser.parse(bytes, 0, bytes.length);
        BerTlvLogger.log("    ", tlvs, LOG);
    }
}
