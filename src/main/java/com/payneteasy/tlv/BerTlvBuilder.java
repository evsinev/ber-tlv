package com.payneteasy.tlv;


import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class BerTlvBuilder {

    private static final Charset ASCII = Charset.forName("US-ASCII");
    private static final BigDecimal HUNDRED = new BigDecimal(100);
    private static final int DEFAULT_SIZE = 5 * 1024;

    public BerTlvBuilder() {
        this((BerTag)null);
    }

    public BerTlvBuilder(BerTag aTemplate) {
        this(aTemplate, new byte[DEFAULT_SIZE], 0, DEFAULT_SIZE);
    }


    public BerTlvBuilder(BerTlvs tlvs) {
        this((BerTag)null);
        for (BerTlv tlv : tlvs.getList()) {
            addBerTlv(tlv);
        }
    }

    public BerTlvBuilder(BerTag aTemplate, byte[] aBuffer, int aOffset, int aLength) {
        theTemplate  = aTemplate;
        theBuffer = aBuffer;
        thePos = aOffset;
        theBufferOffset = aOffset;
    }

    public static BerTlvBuilder from(BerTlv aTlv) {
        if(aTlv.isConstructed()) {
            BerTlvBuilder builder = template(aTlv.getTag());
            for (BerTlv tlv : aTlv.theList) {
                builder.addBerTlv(tlv);
            }
            return builder;
        } else {
            return new BerTlvBuilder().addBerTlv(aTlv);
        }
    }

    public static BerTlvBuilder template(BerTag aTemplate) {
        return new BerTlvBuilder(aTemplate);
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
        BigDecimal numeric = aAmount.multiply(HUNDRED);
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

        if (theTemplate != null) {

            int tagLen = theTemplate.bytes.length;
            int lengthBytesCount = calculateBytesCountForLength(thePos);

            // shifts array
            System.arraycopy(theBuffer, theBufferOffset, theBuffer, tagLen + lengthBytesCount, thePos);

            // copies tag
            System.arraycopy(theTemplate.bytes, 0, theBuffer, theBufferOffset, theTemplate.bytes.length);

            fillLength(theBuffer, tagLen, thePos);

            thePos += tagLen + lengthBytesCount;
        }
        return thePos;
    }

    private void fillLength(byte[] aBuffer, int aOffset, int aLength) {

        if(aLength < 0x80) {
            aBuffer[aOffset] = (byte) aLength;

        } else if (aLength <0x100) {
            aBuffer[aOffset] = (byte) 0x81;
            aBuffer[aOffset+1] = (byte) aLength;

        } else if( aLength < 0x10000) {

            aBuffer[aOffset]   = (byte) 0x82;
            aBuffer[aOffset+1] = (byte) (aLength / 0x100);
            aBuffer[aOffset+2] = (byte) (aLength % 0x100);

        } else if( aLength < 0x1000000 ) {
            aBuffer[aOffset]   = (byte) 0x83;
            aBuffer[aOffset+1] = (byte) (aLength / 0x10000);
            aBuffer[aOffset+2] = (byte) (aLength / 0x100);
            aBuffer[aOffset+3] = (byte) (aLength % 0x100);
        } else {
            throw new IllegalStateException("length ["+aLength+"] out of range (0x1000000)");
        }
    }

    private int calculateBytesCountForLength(int aLength) {
        int ret;
        if(aLength < 0x80) {
            ret = 1;
        } else if (aLength <0x100) {
            ret = 2;
        } else if( aLength < 0x10000) {
            ret = 3;
        } else if( aLength < 0x1000000 ) {
            ret = 4;
        } else {
            throw new IllegalStateException("length ["+aLength+"] out of range (0x1000000)");
        }
        return ret;
    }

    public BerTlvBuilder addHex(BerTag aObject, String aHex) {
        byte[] buffer = HexUtil.parseHex(aHex);
        return addBytes(aObject, buffer, 0, buffer.length);
    }

    public BerTlvBuilder addBytes(BerTag aObject, byte[] aBytes) {
        return addBytes(aObject, aBytes, 0, aBytes.length);
    }

    public BerTlvBuilder addBytes(BerTag aTag, byte[] aBytes, int aFrom, int aLength) {
        int tagLength        = aTag.bytes.length;
        int lengthBytesCount = calculateBytesCountForLength(aLength);

        // TAG
        System.arraycopy(aTag.bytes, 0, theBuffer, thePos, tagLength);
        thePos+=tagLength;

        // LENGTH
        fillLength(theBuffer, thePos, aLength);
        thePos += lengthBytesCount;

        // VALUE
        System.arraycopy(aBytes, aFrom, theBuffer, thePos, aLength);
        thePos+=aLength;

        return this;
    }

    public BerTlvBuilder add(BerTlvBuilder aBuilder) {
        byte[] array = aBuilder.buildArray();
        System.arraycopy(array, 0, theBuffer, thePos, array.length);
        thePos+=array.length;
        return this;
    }


    public BerTlvBuilder addBerTlv(BerTlv aTlv) {
        if(aTlv.isConstructed()) {
            return add(from(aTlv));
        } else {
            return addBytes(aTlv.getTag(), aTlv.getBytesValue());
        }
    }

    /**
     * Add ASCII text
     *
     * @param aTag   tag
     * @param aText  text
     * @return builder
     */
    public BerTlvBuilder addText(BerTag aTag, String aText) {
        return addText(aTag, aText, ASCII);
    }

    /**
     * Add ASCII text
     *
     * @param aTag   tag
     * @param aText  text
     * @return builder
     */
    public BerTlvBuilder addText(BerTag aTag, String aText, Charset aCharset) {
        byte[] buffer = aText.getBytes(aCharset);
        return addBytes(aTag, buffer, 0, buffer.length);
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

    public BerTlv buildTlv() {
        int count = build();
        return new BerTlvParser().parseConstructed(theBuffer, theBufferOffset, count);
    }

    public BerTlvs buildTlvs() {
        int count = build();
        return new BerTlvParser().parse(theBuffer, theBufferOffset, count);
    }

    private final int theBufferOffset;
    private int theLengthPosition;
    private int thePos;
    private final byte[] theBuffer;
    private final BerTag theTemplate;
}

