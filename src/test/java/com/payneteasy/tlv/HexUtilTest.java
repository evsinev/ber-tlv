package com.payneteasy.tlv;

import org.junit.Test;

public class HexUtilTest {

    @Test
    public void testBytes() {
        for(int i=0; i<HexUtil.BYTES.length; i++) {
            if(i%(128/8)==0) {
                System.out.println();
            }
            System.out.print(String.format(", %2d", HexUtil.BYTES[i]));
        }
        System.out.println();
        System.out.println("HexUtil.BYTES.length = " + HexUtil.BYTES.length);
    }
}
