package com.payneteasy.tlv;

import java.util.Arrays;

public class BerTag {
    public final byte[] bytes;

    /**
     * Creates a new tag from given byte array. Similar {@link BerTag#BerTag(byte[], int, int)} but using
     * the full array.
     *
     * @param aBuf to create the tag
     */
    public BerTag(byte[] aBuf) {
        this(aBuf, 0, aBuf.length);
    }

    public BerTag(byte[] aBuf, int aOffset, int aLength) {
        byte[] temp = new byte[aLength];
        System.arraycopy(aBuf, aOffset, temp, 0, aLength);
        bytes = temp;
    }

    public BerTag(int aFirstByte, int aSecondByte) {
        bytes = new byte[]{(byte) (aFirstByte), (byte) aSecondByte};
    }

    public BerTag(int aFirstByte, int aSecondByte, int aFirth) {
        bytes = new byte[]{(byte) (aFirstByte), (byte) aSecondByte, (byte) aFirth};
    }

    public BerTag(int aFirstByte) {
        bytes = new byte[]{(byte) aFirstByte};
    }

    public boolean isConstructed() {
        return (bytes[0] & 0x20) != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BerTag berTag = (BerTag) o;

        return Arrays.equals(bytes, berTag.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return (isConstructed() ? "+ " : "- ") + HexUtil.toHexString(bytes, 0, bytes.length);
    }
}

