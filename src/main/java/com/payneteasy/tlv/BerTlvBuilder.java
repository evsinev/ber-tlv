package com.payneteasy.tlv;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class BerTlvBuilder {

    public BerTlvBuilder(byte[] aBuffer, int aOffset, int aLength) {
        theBuffer = aBuffer;
        thePos = aOffset;
    }

    public BerTlvBuilder() {
        this(new byte[1024], 0, 1024);
    }

    public BerTlvBuilder addTemplate(BerTag aTemplate) {
        theTemplate = true;
        // type
        int len = aTemplate.bytes.length;
        System.arraycopy(aTemplate.bytes, 0, theBuffer, thePos, len);
        thePos+=len;

        // len
        theLengthPosition = thePos;
        theBuffer[thePos++] = 0;

        // value
        // skipped
        return this;

    }


    public BerTlvBuilder addEmpty(BerTag aObject) {
        return addBytes(aObject, new byte[]{}, 0, 0);
    }

    public BerTlvBuilder addByte(BerTag aObject, byte aByte) {
        // type
        int len = aObject.bytes.length;
        System.arraycopy(aObject.bytes, 0, theBuffer, thePos, len);
        thePos+=len;

        // len
        theBuffer[thePos++] = 1;

        // value
        theBuffer[thePos++] = aByte;
        return this;
    }

    public BerTlvBuilder addAmount(BerTag aObject, BigDecimal aAmount) {
        BigDecimal numeric = aAmount.multiply(new BigDecimal(100));
        StringBuilder sb = new StringBuilder(12);
        sb.append(numeric.longValue());
        while(sb.length() < 12) {
            sb.insert(0, '0');
        }
        return addHex(aObject, sb.toString());
    }

    public BerTlvBuilder addDate(BerTag aObject, Date aDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        return addHex(aObject, format.format(aDate));
    }

    public BerTlvBuilder addTime(BerTag aObject, Date aDate) {
        SimpleDateFormat format = new SimpleDateFormat("HHmmss");
        return addHex(aObject, format.format(aDate));
    }

    public int build() {

        if(theTemplate) {
            theBuffer [theLengthPosition] = (byte) (thePos - theLengthPosition - 1 );
        }
        return thePos;
    }

    public BerTlvBuilder addHex(BerTag aObject, String aHex) {
        byte[] buffer = HexUtil.parseHex(aHex);
        return addBytes(aObject, buffer, 0, buffer.length);
    }

    public BerTlvBuilder addBytes(BerTag aObject, byte[] aBytes) {
        return addBytes(aObject, aBytes, 0, aBytes.length);
    }

    public BerTlvBuilder addBytes(BerTag aObject, byte[] aBytes, int aFrom, int aLength) {
        // type
        int typeLen = aObject.bytes.length;
        System.arraycopy(aObject.bytes, 0, theBuffer, thePos, typeLen);
        thePos+=typeLen;

        // len
        theBuffer[thePos++] = (byte) aLength;

        // value
        System.arraycopy(aBytes, aFrom, theBuffer, thePos, aLength);
        thePos+=aLength;
        return this;
    }

    public BerTlvBuilder addBerTlv(BerTlv aTlv) {
        return addBytes(aTlv.getTag(), aTlv.getBytes());
    }

    public BerTlvBuilder addIntAsHex(BerTag aObject, int aCode, int aLength) {
        StringBuilder sb = new StringBuilder(aLength*2);
        sb.append(aCode);
        while(sb.length()<aLength*2) {
            sb.insert(0, '0');
        }
        return addHex(aObject, sb.toString());
    }

    public byte[] buildArray() {
        int count = build();
        byte[] buf = new byte[count];
        System.arraycopy(theBuffer, 0, buf, 0, count);
        return buf;
    }

    private int theLengthPosition;
    private int thePos;
    private final byte[] theBuffer;
    private boolean theTemplate = false;
}

