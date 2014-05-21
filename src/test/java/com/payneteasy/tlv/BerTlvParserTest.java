package com.payneteasy.tlv;

import org.junit.Assert;
import org.junit.Test;

public class BerTlvParserTest {

    public static final BerTag TAG_DF0D_ID = new BerTag(0xdf, 0x0d);
    public static final BerTag TAG_DF7F_VERSION = new BerTag(0xdf, 0x7f);
    private static final BerTlvLoggerSlf4j LOG = new BerTlvLoggerSlf4j();
    public static final BerTag T_EF = new BerTag(0xEF);

    @Test
    public void testParse() {
        String hex = "50 04 56 49 53 41 57 13 10 00 02 31 00 00 00 33 d4 41 22 01 10 03 40 00 00 48 1f 5a 08 10 00 02 31 00 00 00 33 5f 20 1a 43 41 52 44 33 2f 45 4d 56 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 5f 24 03 44 12 31 5f 28 02 06 43 5f 2a 02 06 43 5f 30 02 02 01 5f 34 01 06 82 02 5c 00 84 07 a0 00 00 00 03 10 10 95 05 40 80 00 80 00 9a 03 14 02 10 9b 02 e8 00 9c 01 00 9f 02 06 00 00 00 03 01 04 9f 03 06 00 00 00 00 00 00 9f 06 07 a0 00 00 00 03 10 10 9f 09 02 00 8c 9f 10 07 06 01 0a 03 a0 a1 00 9f 1a 02 08 26 9f 1c 08 30 36 30 34 35 33 39 30 9f 1e 08 30 36 30 34 35 33 39 30 9f 26 08 cb fc 76 79 77 11 1f 15 9f 27 01 80 9f 33 03 e0 b8 c8 9f 34 03 5e 03 00 9f 35 01 22 9f 36 02 00 0e 9f 37 04 46 1b da 7c 9f 41 04 00 00 00 63";
        parse(hex);
    }

    private BerTlvs parse(String hex) {
        byte[] bytes = HexUtil.parseHex(hex);

        BerTlvParser parser = new BerTlvParser(LOG);
        BerTlvs tlvs = parser.parse(bytes, 0, bytes.length);
        BerTlvLogger.log("    ", tlvs, LOG);
        return tlvs;
    }

    @Test
    public void testParseLen2() {
         parse("E08191717F9F180412345678860D842400\n" +
                 "                      00085AE957E8818D95A8860D84240000\n" +
                 "                      0836D244954747EC1E860D8424000008\n" +
                 "                      3863B1C179BE38AC860D842400000825\n" +
                 "                      4DB4B4ECDB2174860D8424000008678D\n" +
                 "                      F9128478E28F860D8424000008514C8F\n" +
                 "                      D95A216C0B860D84240000081F623465\n" +
                 "                      DB0C9559860D84240000082A5A0A9A82\n" +
                 "                      8ABA0A910A42500E4381A4677A30308A\n" +
                 "                      023030\n");


    }


    @Test
    public void testMulti() {
        BerTlvs tlvs = parse("e1 35 9f 1e 08 31 36 30 32 31 34 33 37 ef 12 df 0d 08 4d 30 30 30 2d 4d 50 49 df 7f 04 31 2d 32 32 ef 14 df 0d 0b 4d 30 30 30 2d 54 45 53 54 4f 53 df 7f 03 36 2d 35");
        Assert.assertNotNull(tlvs.find(new BerTag(0xe1)));
        Assert.assertEquals("1 6 0 2 1 4 3 7".replace(" ", ""), tlvs.find(new BerTag(0x9f, 0x1e)).getTextValue());
        Assert.assertNotNull(tlvs.find(T_EF));
        Assert.assertEquals(2, tlvs.findAll(T_EF).size());

        // first EF
        BerTlv firstEf = tlvs.find(T_EF);
        Assert.assertNotNull(firstEf);
        Assert.assertNotNull(firstEf.find(TAG_DF0D_ID));
        Assert.assertEquals("M000-MPI", firstEf.find(TAG_DF0D_ID).getTextValue());
        Assert.assertNotNull("No EF / DF7F tag found", firstEf.find(TAG_DF7F_VERSION));
        Assert.assertEquals("1-22", firstEf.find(TAG_DF7F_VERSION).getTextValue());


    }
}
