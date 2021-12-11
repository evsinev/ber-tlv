package com.payneteasy.tlv;


import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BerTlvParser {

	private static final BerTagFactory DEFAULT_TAG_FACTORY = new DefaultBerTagFactory();

	private final BerTagFactory tagFactory;
    private final IBerTlvLogger log;

    public BerTlvParser() {
        this(DEFAULT_TAG_FACTORY, EMPTY_LOGGER);
    }

    public BerTlvParser(IBerTlvLogger aLogger) {
    	this(DEFAULT_TAG_FACTORY, aLogger);
    }
    
    public BerTlvParser(BerTagFactory aTagFactory) {
    	this(aTagFactory, EMPTY_LOGGER);
    }
    
    public BerTlvParser(BerTagFactory aTagFactory, IBerTlvLogger aLogger) {
    	tagFactory = aTagFactory;
    	log = aLogger;
    }
    
    public BerTlv parseConstructed(byte[] aBuf) {
        return parseConstructed(aBuf, 0, aBuf.length);
    }

    public BerTlv parseConstructed(byte[] aBuf, int aOffset, int aLen) {
        ParseResult result =  parseWithResult(0, aBuf, aOffset, aLen);
        return result.tlv;
    }

    public BerTlvs parse(byte[] aBuf) {
        return parse(aBuf, 0, aBuf.length);
    }

    public BerTlvs parse(byte[] aBuf, final int aOffset, int aLen) {
        List<BerTlv> tlvs = new ArrayList<BerTlv>();
        if(aLen==0) return new BerTlvs(tlvs);

        int offset = aOffset;
        for(int i=0; i<100; i++) {
            ParseResult result =  parseWithResult(0, aBuf, offset, aLen-offset);

            if (!result.ignoreTag) {
                tlvs.add(result.tlv);
            }

            if(result.offset>=aOffset+aLen) {
                break;
            }

            offset = result.offset;

        }

        return new BerTlvs(tlvs);
    }

    private ParseResult parseWithResult(int aLevel, byte[] aBuf, int aOffset, int aLen) {
        String levelPadding = createLevelPadding(aLevel);
        if(aOffset+aLen > aBuf.length) {
            throw new IllegalStateException("Length is out of the range [offset="+aOffset+",  len="+aLen+", array.length="+aBuf.length+", level="+aLevel+"]");
        }
        if(log.isDebugEnabled()) {
            log.debug("{}parseWithResult(level={}, offset={}, len={}, buf={})", levelPadding, aLevel, aOffset, aLen, HexUtil.toFormattedHexString(aBuf, aOffset, aLen));
        }

        // tag
        int tagBytesCount = getTagBytesCount(aBuf, aOffset);
        BerTag tag        = createTag(levelPadding, aBuf, aOffset, tagBytesCount);
        if(log.isDebugEnabled()) {
            log.debug("{}tag = {}, tagBytesCount={}, tagBuf={}", levelPadding, tag, tagBytesCount, HexUtil.toFormattedHexString(aBuf, aOffset, tagBytesCount));
        }

        // check invalid 00 tag byte with no meaning.
        if ((tag.bytes[0] & 0xFF) == 0x00) {
            return new ParseResult(null, aOffset + 1, true);
        }

        // length
        int lengthBytesCount  = getLengthBytesCount(aBuf, aOffset + tagBytesCount);
        int valueLength       = getDataLength(aBuf, aOffset + tagBytesCount);

        if(log.isDebugEnabled()) {
            log.debug("{}lenBytesCount = {}, len = {}, lenBuf = {}"
                    , levelPadding, lengthBytesCount, valueLength, HexUtil.toFormattedHexString(aBuf, aOffset + tagBytesCount, lengthBytesCount));
        }

        // value
        if(tag.isConstructed()) {

            ArrayList<BerTlv> list = new ArrayList<BerTlv>();
            addChildren(aLevel, aBuf, aOffset + tagBytesCount + lengthBytesCount, levelPadding, lengthBytesCount, valueLength, list);

            int resultOffset = aOffset + tagBytesCount + lengthBytesCount + valueLength;
            if(log.isDebugEnabled()) {
                log.debug("{}returning constructed offset = {}", levelPadding, resultOffset);
            }
            return new ParseResult(new BerTlv(tag, list), resultOffset, false);
        } else {
            // value
            byte[] value = new byte[valueLength];
            System.arraycopy(aBuf, aOffset+tagBytesCount+lengthBytesCount, value, 0, valueLength);
            int resultOffset = aOffset + tagBytesCount + lengthBytesCount + valueLength;
            if(log.isDebugEnabled()) {
                log.debug("{}value = {}", levelPadding, HexUtil.toFormattedHexString(value));
                log.debug("{}returning primitive offset = {}", levelPadding, resultOffset);
            }
            return new ParseResult(new BerTlv(tag, value), resultOffset, false);
        }

    }

    /**
     *
     * @param aLevel          level for debug
     * @param aBuf            buffer
     * @param aOffset         offset (first byte)
     * @param levelPadding    level padding (for debug)
     * @param aDataBytesCount data bytes count
     * @param valueLength     length
     * @param list            list to add
     */
    private void addChildren(int aLevel, byte[] aBuf, int aOffset, String levelPadding, int aDataBytesCount, int valueLength, ArrayList<BerTlv> list) {
        int startPosition = aOffset;
        int len = valueLength;
        while (startPosition < aOffset + valueLength ) {
            ParseResult result = parseWithResult(aLevel+1, aBuf, startPosition, len);
            list.add(result.tlv);

            startPosition = result.offset;
            len           = (aOffset + valueLength) - startPosition;

            if(log.isDebugEnabled()) {
                log.debug("{}level {}: adding {} with offset {}, startPosition={}, aDataBytesCount={}, valueLength={}"
                        , levelPadding, aLevel, result.tlv.getTag(), result.offset, startPosition, aDataBytesCount, valueLength);
            }
        }
    }

    private String createLevelPadding(int aLevel) {
        if(!log.isDebugEnabled()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for(int i=0; i<aLevel*4; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static class ParseResult {
        public ParseResult(BerTlv aTlv, int aOffset, boolean aIgnoreTag) {
            tlv = aTlv;
            offset = aOffset;
            ignoreTag = aIgnoreTag;
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
        private final boolean ignoreTag;
    }


    private BerTag createTag(String aLevelPadding, byte[] aBuf, int aOffset, int aLength) {
        if(log.isDebugEnabled()) {
            log.debug("{}Creating tag {}...", aLevelPadding, HexUtil.toFormattedHexString(aBuf, aOffset, aLength));
        }
        return tagFactory.createTag(aBuf, aOffset, aLength);
    }

    private int getTagBytesCount(byte[] aBuf, int aOffset) {
        if((aBuf[aOffset] & 0x1F) == 0x1F) { // see subsequent bytes
            int len = 2;
            for(int i=aOffset+1; i<aOffset+10; i++) {
                if( (aBuf[i] & 0x80) != 0x80) {
                    break;
                }
                len++;
            }
            return len;
        }

        return 1;
    }


    private int getDataLength(byte[] aBuf, int aOffset) {

        int length = aBuf[aOffset] & 0xff;

        if((length & 0x80) == 0x80) {
            int numberOfBytes = length & 0x7f;
            if(numberOfBytes>3) {
                throw new IllegalStateException(String.format("At position %d the len is more then 3 [%d]", aOffset, numberOfBytes));
            }

            length = 0;
            for(int i=aOffset+1; i<aOffset+1+numberOfBytes; i++) {
                length = length * 0x100 + (aBuf[i] & 0xff);
            }

        }
        return length;
    }

    private static int getLengthBytesCount(byte aBuf[], int aOffset) {

        int len = aBuf[aOffset] & 0xff;
        if( (len & 0x80) == 0x80) {
            return 1 + (len & 0x7f);
        } else {
            return 1;
        }
    }


    private static final IBerTlvLogger EMPTY_LOGGER = new IBerTlvLogger() {
        public boolean isDebugEnabled() {
            return false;
        }

        public void debug(String aFormat, Object... args) {
        }
    };


}