package com.payneteasy.tlv;


import java.util.ArrayList;

/**
 *
 */
public class BerTlvParser {

    private final IBerTlvLogger log;

    public BerTlvParser(IBerTlvLogger aLogger) {
        log = aLogger;
    }

    public BerTlv parseConstructed(byte[] aBuf, int aOffset, int aLen) {
        ParseResult result =  parseWithResult(0, aBuf, aOffset, aLen);
        return result.tlv;
    }

    public BerTlvs parse(byte[] aBuf, final int aOffset, int aLen) {
        BerTlvs tlvs = new BerTlvs();
        if(aLen==0) return tlvs;

        int offset = aOffset;
        for(int i=0; i<100; i++) {
            ParseResult result =  parseWithResult(0, aBuf, offset, aLen-offset);
            tlvs.add(result.tlv);

            if(result.offset>=aOffset+aLen) {
                break;
            }

            offset = result.offset;

        }

        return tlvs;
    }

    private ParseResult parseWithResult(int aLevel, byte[] aBuf, int aOffset, int aLen) {
        String levelPadding = createLevelPadding(aLevel);
        if(aOffset+aLen > aBuf.length) {
            throw new IllegalStateException("Length is out of the range [offset="+aOffset+",  len="+aLen+", array.length="+aBuf.length+", level="+aLevel+"]");
        }
        if(log.isDebugEnabled()) {
            log.debug("{}parseWithResult(level={}, offset={}, len={}, buf={})", levelPadding, aLevel, aOffset, aLen, HexUtil.toHexString(aBuf, aOffset, aLen));
        }

        // tag
        int tagOffset = getTagOffset(aBuf, aOffset);
        BerTag tag = findTag(aBuf, aOffset, tagOffset);
        if(log.isDebugEnabled()) {
            log.debug("{}    tag = {}, buf={}", levelPadding, tag, HexUtil.toHexString(aBuf, aOffset, tagOffset));
        }

        // length
        int dataOffset = getDataOffset(aBuf, aOffset + tagOffset);
        int valueLength = getDataLength(aBuf, aOffset + tagOffset);

        if(log.isDebugEnabled()) {
            log.debug("{}    len = {}, buf = {}"
                    , levelPadding, valueLength, HexUtil.toHexString(aBuf, aOffset + tagOffset, dataOffset));
        }

        // value
        if(tag.isConstructed()) {

            ArrayList<BerTlv> list = new ArrayList<BerTlv>();
            addChildren(aLevel, aBuf, aOffset, levelPadding, tagOffset, dataOffset, valueLength, list);

            int resultOffset = aOffset + tagOffset + dataOffset + valueLength;
            if(log.isDebugEnabled()) {
                log.debug("{}    returning constructed offset = {}", levelPadding, resultOffset);
            }
            return new ParseResult(new BerTlv(tag, list), resultOffset);
        } else {
            // value
            byte[] value = new byte[valueLength];
            System.arraycopy(aBuf, aOffset+tagOffset+dataOffset, value, 0, valueLength);
            int resultOffset = aOffset + tagOffset + dataOffset + valueLength;
            if(log.isDebugEnabled()) {
                log.debug("{}    returning primitive offset = {}", levelPadding, resultOffset);
            }
            return new ParseResult(new BerTlv(tag, value), resultOffset);
        }

    }

    private void addChildren(int aLevel, byte[] aBuf, int aOffset, String levelPadding, int tagOffset, int dataOffset, int valueLength, ArrayList<BerTlv> list) {
        int conOffset = aOffset+tagOffset+dataOffset;
        int conLen = valueLength;
        while( conOffset < dataOffset+valueLength) {
            ParseResult result = parseWithResult(aLevel+1, aBuf, conOffset, conLen);
            if(log.isDebugEnabled()) {
                log.debug("{}    level {}: adding {} with offset {}", levelPadding, aLevel, result.tlv.getTag(), result.offset);
            }
            conOffset = result.offset;
            conLen = valueLength-conOffset;
            list.add(result.tlv);
        }
    }

    private String createLevelPadding(int aLevel) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<aLevel*4; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static class ParseResult {
        public ParseResult(BerTlv aTlv, int aOffset) {
            tlv = aTlv;
            offset = aOffset;
        }

        @Override
        public String toString() {
            return "ParseResult{" +
                    "tlv=" + tlv +
                    ", offset=" + offset +
                    '}';
        }

        private final BerTlv tlv;
        private final int offset;
    }


    private BerTag findTag(byte[] aBuf, int aOffset, int aLength) {
        if(log.isDebugEnabled()) {
            log.debug("Creating tag {}", HexUtil.toHexString(aBuf, aOffset, aLength));
        }
        return new BerTag(aBuf, aOffset, aLength);
    }

    private int getTagOffset(byte[] aBuf, int aOffset) {
        if((aBuf[aOffset] & 0x1F) == 0x1F) { // see subsequent bytes
            int len = 2;
            for(int i=aOffset+1; i<aOffset+10; i++) {
                if( (aBuf[i] & 0x80) != 0x80) {
                    break;
                }
                len++;
            }
            return len;
        } else {
            return 1;
        }
    }


    private int getDataLength(byte[] aBuf, int aOffset) {

        int length = aBuf[aOffset] & 0xff;

        if((length & 0x80) == 0x80) {
            int numberOfBytes = length & 0x7f;
            if(numberOfBytes>3) throw new IllegalStateException("Bad number of bytes "+numberOfBytes);

            length = 0;
            for(int i=aOffset+1; i<aOffset+1+numberOfBytes; i++) {
                length = length * 0x100 + (aBuf[i] & 0xff);
            }

        }
        return length;
    }

    private static int getDataOffset(byte aBuf[], int aOffset) {

        int len = aBuf[aOffset] & 0xff;
        if( (len & 0x80) == 0x80) {
            return 1 + (len & 0x7f);
        } else {
            return 1;
        }
    }


}