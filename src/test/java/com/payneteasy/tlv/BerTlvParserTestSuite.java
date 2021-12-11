package com.payneteasy.tlv;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Suite.class)
@Suite.SuiteClasses({BerTlvParserTestSuite.BerTlvParserTest.class, BerTlvParserTestSuite.TestIgnoreTagByteWithNoMeaning.class})
public class BerTlvParserTestSuite {

    public static final BerTag TAG_DF0D_ID = new BerTag(0xdf, 0x0d);
    public static final BerTag TAG_DF7F_VERSION = new BerTag(0xdf, 0x7f);
    private static final BerTlvLoggerSlf4j LOG = new BerTlvLoggerSlf4j();
    public static final BerTag T_EF = new BerTag(0xEF);

    public static class BerTlvParserTest {
        @Test
        public void testParse() {
            String hex =
                    /*            0  1  2  3   4  5  6  7     8  9  a  b   c  d  e  f      0123 4567  89ab  cdef */
                    /*    0 */   "50 04 56 49  53 41 57 13    10 00 02 31  00 00 00 33" // P.VI SAW.  ...1  ...3
                    /*   10 */ + "d4 41 22 01  10 03 40 00    00 48 1f 5a  08 10 00 02" // .A". ..@.  .H.Z  ....
                    /*   20 */ + "31 00 00 00  33 5f 20 1a    43 41 52 44  33 2f 45 4d" // 1... 3_ .  CARD  3/EM
                    /*   30 */ + "56 20 20 20  20 20 20 20    20 20 20 20  20 20 20 20" // V
                    /*   40 */ + "20 20 5f 24  03 44 12 31    5f 28 02 06  43 5f 2a 02" //   _$ .D.1  _(..  C_*.
                    /*   50 */ + "06 43 5f 30  02 02 01 5f    34 01 06 82  02 5c 00 84" // .C_0 ..._  4...  .\..
                    /*   60 */ + "07 a0 00 00  00 03 10 10    95 05 40 80  00 80 00 9a" // .... ....  ..@.  ....
                    /*   70 */ + "03 14 02 10  9b 02 e8 00    9c 01 00 9f  02 06 00 00" // .... ....  ....  ....
                    /*   80 */ + "00 03 01 04  9f 03 06 00    00 00 00 00  00 9f 06 07" // .... ....  ....  ....
                    /*   90 */ + "a0 00 00 00  03 10 10 9f    09 02 00 8c  9f 10 07 06" // .... ....  ....  ....
                    /*   a0 */ + "01 0a 03 a0  a1 00 9f 1a    02 08 26 9f  1c 08 30 36" // .... ....  ..&.  ..06
                    /*   b0 */ + "30 34 35 33  39 30 9f 1e    08 30 36 30  34 35 33 39" // 0453 90..  .060  4539
                    /*   c0 */ + "30 9f 26 08  cb fc 76 79    77 11 1f 15  9f 27 01 80" // 0.&. ..vy  w...  .'..
                    /*   d0 */ + "9f 33 03 e0  b8 c8 9f 34    03 5e 03 00  9f 35 01 22" // .3.. ...4  .^..  .5."
                    /*   e0 */ + "9f 36 02 00  0e 9f 37 04    46 1b da 7c  9f 41 04 00" // .6.. ..7.  F..|  .A..
                    /*   f0 */ + "00 00 63                                            " // ..c
                    ;
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
            String hex =
                    /*            0  1  2  3   4  5  6  7     8  9  a  b   c  d  e  f      0123 4567  89ab  cdef */
                    /*    0 */   "e0 81 91 71  7f 9f 18 04    12 34 56 78  86 0d 84 24" // ...q ....  .4Vx  ...$
                    /*   10 */ + "00 00 08 5a  e9 57 e8 81    8d 95 a8 86  0d 84 24 00" // ...Z .W..  ....  ..$.
                    /*   20 */ + "00 08 36 d2  44 95 47 47    ec 1e 86 0d  84 24 00 00" // ..6. D.GG  ....  .$..
                    /*   30 */ + "08 38 63 b1  c1 79 be 38    ac 86 0d 84  24 00 00 08" // .8c. .y.8  ....  $...
                    /*   40 */ + "25 4d b4 b4  ec db 21 74    86 0d 84 24  00 00 08 67" // %M.. ..!t  ...$  ...g
                    /*   50 */ + "8d f9 12 84  78 e2 8f 86    0d 84 24 00  00 08 51 4c" // .... x...  ..$.  ..QL
                    /*   60 */ + "8f d9 5a 21  6c 0b 86 0d    84 24 00 00  08 1f 62 34" // ..Z! l...  .$..  ..b4
                    /*   70 */ + "65 db 0c 95  59 86 0d 84    24 00 00 08  2a 5a 0a 9a" // e... Y...  $...  *Z..
                    /*   80 */ + "82 8a ba 0a  91 0a 42 50    0e 43 81 a4  67 7a 30 30" // .... ..BP  .C..  gz00
                    /*   90 */ + "8a 02 30 30                                         " // ..00
                    ;
            parse(hex);
        }


        @Test
        public void testMulti() {
            String hex =
                    /*            0  1  2  3   4  5  6  7     8  9  a  b   c  d  e  f      0123 4567  89ab  cdef */
                    /*    0 */   "e1 35 9f 1e  08 31 36 30    32 31 34 33  37 ef 12 df" // .5.. .160  2143  7...
                    /*   10 */ + "0d 08 4d 30  30 30 2d 4d    50 49 df 7f  04 31 2d 32" // ..M0 00-M  PI..  .1-2
                    /*   20 */ + "32 ef 14 df  0d 0b 4d 30    30 30 2d 54  45 53 54 4f" // 2... ..M0  00-T  ESTO
                    /*   30 */ + "53 df 7f 03  36 2d 35                               " // S... 6-5
                    ;
            BerTlvs tlvs = parse(hex);
            Assert.assertNotNull(tlvs.find(new BerTag(0xe1)));
            Assert.assertEquals("1 6 0 2 1 4 3 7".replace(" ", ""), tlvs.find(new BerTag(0x9f, 0x1e)).getTextValue());
            Assert.assertNotNull(tlvs.find(T_EF));
            Assert.assertEquals(2, tlvs.findAll(T_EF).size());

            // first EF
            {
                BerTlv firstEf = tlvs.find(T_EF);
                Assert.assertNotNull(firstEf);
                Assert.assertNotNull(firstEf.find(TAG_DF0D_ID));
                Assert.assertEquals("M000-MPI", firstEf.find(TAG_DF0D_ID).getTextValue());
                Assert.assertNotNull("No EF / DF7F tag found", firstEf.find(TAG_DF7F_VERSION));
                Assert.assertEquals("1-22", firstEf.find(TAG_DF7F_VERSION).getTextValue());
            }

            // second EF
            BerTlv secondEf = tlvs.findAll(T_EF).get(1);
            Assert.assertNotNull(secondEf);
            Assert.assertNotNull(secondEf.find(TAG_DF0D_ID));
            Assert.assertEquals("M000-TESTOS", secondEf.find(TAG_DF0D_ID).getTextValue());
            Assert.assertNotNull("No EF / DF7F tag found", secondEf.find(TAG_DF7F_VERSION));
            Assert.assertEquals("6-5", secondEf.find(TAG_DF7F_VERSION).getTextValue());


        }

        @Test
        public void test_empty_length() {
            BerTlv tlv = new BerTlvParser(LOG).parseConstructed(HexUtil.parseHex("E3 02 01 00"));
            BerTlvLogger.log("    ", tlv, LOG);

            Assert.assertEquals(tlv.getTag(), new BerTag(0xe3));
            Assert.assertNotNull(tlv.getValues());
            Assert.assertEquals(1, tlv.getValues().size());
            BerTlv emptyTag = tlv.getValues().get(0);
            Assert.assertEquals(new BerTag(0x01), emptyTag.getTag());
            Assert.assertEquals("", emptyTag.getHexValue());
        }

        @Test
        public void test_empty_hex() {
            byte[] bytes = HexUtil.parseHex("");
            BerTlvParser parser = new BerTlvParser(LOG);
            BerTlvs tlvs = parser.parse(bytes, 0, bytes.length);
            Assert.assertEquals(0, tlvs.getList().size());
        }

        @Test
        public void test_issue_10_last_tag() {
            String hex = "BF01820114DF0105A0000000049F2201EFDF020101DF030101DF0481F8A191CB87473F29349B5D60A88B3EAEE0973AA6F1A082F358D849FDDFF9C091F899EDA9792CAF09EF28F5D22404B88A2293EEBBC1949C43BEA4D60CFD879A1539544E09E0F09F60F065B2BF2A13ECC705F3D468B9D33AE77AD9D3F19CA40F23DCF5EB7C04DC8F69EBA565B1EBCB4686CD274785530FF6F6E9EE43AA43FDB02CE00DAEC15C7B8FD6A9B394BABA419D3F6DC85E16569BE8E76989688EFEA2DF22FF7D35C043338DEAA982A02B866DE5328519EBBCD6F03CDD686673847F84DB651AB86C28CF1462562C577B853564A290C8556D818531268D25CC98A4CC6A0BDFFFDA2DCCA3A94C998559E307FDDF915006D9A987B07DDAEB3BDF050103";
            byte[] bytes = HexUtil.parseHex(hex);
            BerTlvParser parser = new BerTlvParser();
            BerTlvs tlvs = parser.parse(bytes, 0, bytes.length);

            BerTlvLogger.log("  ", tlvs, LOG);

            {
                BerTlv berTlv = tlvs.find(new BerTag(0xDF, 0x05));
                Assert.assertNotNull(berTlv);
                Assert.assertEquals("03", berTlv.getHexValue());
            }

            {
                Assert.assertEquals(1, tlvs.getList().size());
                Assert.assertTrue(tlvs.getList().get(0).isConstructed());
                Assert.assertEquals(6, tlvs.getList().get(0).getValues().size());
            }
        }

    }

    @RunWith(Parameterized.class)
    public static class TestIgnoreTagByteWithNoMeaning {
        private final String testTlv;
        private final String expectedTlv;

        public TestIgnoreTagByteWithNoMeaning(String testTlv, String expectedTlv) {
            this.testTlv = testTlv;
            this.expectedTlv = expectedTlv;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> ignoreBytesNoMeaningTestCases() {
            return Arrays.asList(new Object[][] {
                    {
                            "500057104761731000000092D2412201184348255A0847617310000000925F24005F2A0204845F3401018202000084008A00950500000000009A032112039B0200009C01009F02060000000354009F03060000000000009F0607A00000000310109F0902008C9F100706010A03A000009F1A0203569F1E0838333230314943439F260843B701BC8173ED4A9F2701809F3303E0F8C89F34031F03009F3501229F360201009F3704E5BC09799F3901079F4104000001629F53009F6E04200000009F7C009F21031806089F6604B68040009F6C020000DF3501059F816E01009F1E08383332303149434300000000000000",
                            "500057104761731000000092D2412201184348255A0847617310000000925F24005F2A0204845F3401018202000084008A00950500000000009A032112039B0200009C01009F02060000000354009F03060000000000009F0607A00000000310109F0902008C9F100706010A03A000009F1A0203569F1E0838333230314943439F260843B701BC8173ED4A9F2701809F3303E0F8C89F34031F03009F3501229F360201009F3704E5BC09799F3901079F4104000001629F53009F6E04200000009F7C009F21031806089F6604B68040009F6C020000DF3501059F816E01009F1E083833323031494343"
                    },
                    {
                            "708201A54F07A0000001523010500A44696E657273436C7562571036542603668525D210760118070000925A0736542603668525820258008407A00000015230108E0E00000000000000001E0302031F03950502400080009A032112109B02E8009C01315F201A41554341544F4D4120472F4C454E494E202020202020202020205F24032107315F25031807135F280202185F2A0201705F300206015F3401019F01060012345678909F02060000000001009F03060000000000009F0607A00000015230109F0702AB009F080200019F090200019F0D05F040C428009F0E0500100000409F0F05F068C4F8009F10080106A000034000009F1101019F120E44494E4552532045435520444F4D9F150212349F160F4243544553543132333435363738399F1A0203569F1C084E4C2D47503733309F1E0838333230314943439F1F1431383037303030303030303030303030303039329F21031245129F26080FCFC8BD97929D889F2701809F3303E0F8C89F34030203009F3501229F360200809F37044D79736B9F3901059F40057000B0A0019F4104000013579F4502DAC09F4E04616263649F53015200000000000000",
                            "708201A54F07A0000001523010500A44696E657273436C7562571036542603668525D210760118070000925A0736542603668525820258008407A00000015230108E0E00000000000000001E0302031F03950502400080009A032112109B02E8009C01315F201A41554341544F4D4120472F4C454E494E202020202020202020205F24032107315F25031807135F280202185F2A0201705F300206015F3401019F01060012345678909F02060000000001009F03060000000000009F0607A00000015230109F0702AB009F080200019F090200019F0D05F040C428009F0E0500100000409F0F05F068C4F8009F10080106A000034000009F1101019F120E44494E4552532045435520444F4D9F150212349F160F4243544553543132333435363738399F1A0203569F1C084E4C2D47503733309F1E0838333230314943439F1F1431383037303030303030303030303030303039329F21031245129F26080FCFC8BD97929D889F2701809F3303E0F8C89F34030203009F3501229F360200809F37044D79736B9F3901059F40057000B0A0019F4104000013579F4502DAC09F4E04616263649F530152"
                    }
            });
        }

        @Test
        public void test_ignore_00_bytes_with_no_meaning() {
            try {
                byte[] tlvBytes = HexUtil.parseHex(testTlv);
                BerTlvParser parser = new BerTlvParser();
                BerTlvs tlvs = parser.parse(tlvBytes, 0, tlvBytes.length);
                BerTlvLogger.log("  ", tlvs, LOG);

                BerTlvBuilder builder = new BerTlvBuilder(tlvs);
                String resolvedTlv = HexUtil.toHexString(builder.buildArray());
                Assert.assertEquals(expectedTlv, resolvedTlv);
            } catch (Exception ex) {
                ex.printStackTrace();
                Assert.fail(ex.getMessage());
            }
        }
    }
}
