package com.payneteasy.tlv;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.payneteasy.tlv.BerTlvBuilder.template;

public class BerTlvBuilderTest {

    public static final BerTag TAG_E0 = new BerTag(0xe0);
    public static final BerTag TAG_86 = new BerTag(0x86);
    public static final BerTag TAG_71 = new BerTag(0x71);

    /**
     * 1 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -50 = 56495341
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -57 = 1000023100000033D44122011003400000481F
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5A = 1000023100000033
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F20 = 43415244332F454D562020202020202020202020202020202020
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F24 = 441231
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F28 = 0643
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F2A = 0643
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F30 = 0201
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F34 = 06
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -82 = 5C00
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -84 = A0000000031010
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -95 = 4080008000
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9A = 140210
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9B = E800
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9C = 00
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F02 = 000000030104
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F03 = 000000000000
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F06 = A0000000031010
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F09 = 008C
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F10 = 06010A03A0A100
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F1A = 0826
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F1C = 3036303435333930
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F1E = 3036303435333930
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F26 = CBFC767977111F15
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F27 = 80
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F33 = E0B8C8
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F34 = 5E0300
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F35 = 22
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F36 = 000E
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F37 = 461BDA7C
     */
    @Test
    public void test() {
        byte[] bytes =  new BerTlvBuilder()
                .addHex(new BerTag(0x50), "56495341")
                .addHex(new BerTag(0x57), "1000023100000033D44122011003400000481F")
                .buildArray();
        Assert.assertArrayEquals(HexUtil.parseHex("50045649534157131000023100000033D44122011003400000481F"), bytes);
    }

    @Test
    public void test91() {
        BerTlvBuilder b = template(TAG_E0);

        for(int i=0; i<10; i++) {
            b.addHex(TAG_86, "F9128478E28F860D8424000008514C8F");
        }
        byte[] buf = b.buildArray();
        System.out.println(HexUtil.toFormattedHexString(buf));
    }

    @Test
    public void testComplex() {
        byte[] bytes = template(TAG_E0)
                .add(
                        template(TAG_71)
                                .addHex(TAG_86, "F9128478E28F860D8424000008514C01")
                                .addHex(TAG_86, "F9128478E28F860D8424000008514C02")
                )
                .addHex(TAG_86, "F9128478E28F860D8424000008514C03")
                .buildArray();
        System.out.println(HexUtil.toFormattedHexString(bytes));

        BerTlv tlv = new BerTlvParser().parseConstructed(bytes, 0, bytes.length);
        List<BerTlv> list = tlv.findAll(TAG_86);
        System.out.println("list = " + list);

    }

    @Test
    public void testAddComplex() {
        byte[] expectedBuffer = HexUtil.parseHex("E08191717F9F180412345678860D842400\n" +
                "                      00085AE957E8818D95A8860D84240000\n" +
                "                      0836D244954747EC1E860D8424000008\n" +
                "                      3863B1C179BE38AC860D842400000825\n" +
                "                      4DB4B4ECDB2174860D8424000008678D\n" +
                "                      F9128478E28F860D8424000008514C8F\n" +
                "                      D95A216C0B860D84240000081F623465\n" +
                "                      DB0C9559860D84240000082A5A0A9A82\n" +
                "                      8ABA0A910A42500E4381A4677A30308A\n" +
                "                      023030\n");
        BerTlv source = new BerTlvParser().parseConstructed(expectedBuffer);

        BerTlvLogger.log("....", source, new BerTlvLoggerSlf4j());
        {
            byte[] out = BerTlvBuilder
                    .from(source)
                    .buildArray();

            BerTlv outTlv = BerTlvBuilder
                    .from(source)
                    .buildTlv();

            BerTlvLogger.log("....", outTlv, new BerTlvLoggerSlf4j());

            Assert.assertArrayEquals(expectedBuffer, out);
        }

        {
            BerTlv outTlv = new BerTlvBuilder().addBerTlv(source).buildTlv();
            byte[] out = new BerTlvBuilder().addBerTlv(source).buildArray();

            BerTlvLogger.log("....", outTlv, new BerTlvLoggerSlf4j());
            Assert.assertArrayEquals(expectedBuffer, out);
        }

    }
}
