package com.payneteasy.tlv;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BerTagTest {

    @Test
    public void testByteArrayConst() {
        assertEquals(new BerTag(0x6f, 0x1c), new BerTag(new byte[]{0x6f, 0x1c}));
        assertEquals(new BerTag(new byte[]{0x6f, 0x1c}, 0, 2), new BerTag(new byte[]{0x6f, 0x1c}));
        assertEquals(new BerTag(0x6f, 0x1c).hashCode(), new BerTag(new byte[]{0x6f, 0x1c}).hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new BerTag(0x01, 0x02, 0x03), new BerTag(new byte[]{0x01, 0x02, 0x03}));
        assertEquals(new BerTag(0x01, 0x02), new BerTag(new byte[]{0x01, 0x02}));
        assertEquals(new BerTag(0x01), new BerTag(new byte[]{0x01}));

        assertNotEquals(new BerTag(0x01), new BerTag(new byte[]{0x02}));
        assertNotEquals(new BerTag(0x01), new BerTag(new byte[]{0x01, 0x01}));
        assertNotEquals(new BerTag(0x01, 0x1), new BerTag(new byte[]{0x02, 0x1}));
    }

    @Test
    public void testHashcode() {
        assertEquals(new BerTag(0x01, 0x02, 0x03).hashCode(), new BerTag(new byte[]{0x01, 0x02, 0x03}).hashCode());
        assertEquals(new BerTag(0x01, 0x02).hashCode(), new BerTag(new byte[]{0x01, 0x02}).hashCode());
        assertEquals(new BerTag(0x01).hashCode(), new BerTag(new byte[]{0x01}).hashCode());

        assertNotEquals(new BerTag(0x01).hashCode(), new BerTag(new byte[]{0x02}).hashCode());
        assertNotEquals(new BerTag(0x01).hashCode(), new BerTag(new byte[]{0x01, 0x01}).hashCode());
        assertNotEquals(new BerTag(0x01, 0x1).hashCode(), new BerTag(new byte[]{0x02, 0x1}).hashCode());
    }
}