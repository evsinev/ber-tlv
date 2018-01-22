package com.payneteasy.tlv;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class BerTlv {

    private final static Charset ASCII = Charset.forName("US-ASCII");

    private final BerTag theTag;
    private final byte[] theValue;
    protected final List<BerTlv> theList;

    /**
     * Creates constructed TLV
     *
     * @param aTag   tag
     * @param aList  set of nested TLVs
     */
    public BerTlv(BerTag aTag, List<BerTlv> aList) {
        theTag = aTag;
        theList = aList;
        theValue = null;
    }

    /**
     * Creates primitive TLV
     *
     * @param aTag   tag
     * @param aValue value as byte[]
     */
    public BerTlv(BerTag aTag, byte[] aValue) {
        theTag = aTag;
        theValue = aValue;
        theList = null;
    }

    //
    //
    //

    public BerTag getTag() {
        return theTag;
    }

    public boolean isPrimitive() {
        return !theTag.isConstructed();
    }

    public boolean isConstructed() {
        return theTag.isConstructed();
    }

    public boolean isTag(BerTag aTag) {
        return theTag.equals(aTag);
    }

    //
    // find
    //

    public BerTlv find(BerTag aTag) {
        if(aTag.equals(getTag())) {
            return this;
        }

        if(isConstructed()) {
            for (BerTlv tlv : theList) {
                BerTlv ret = tlv.find(aTag);
                if(ret!=null) {
                    return ret;
                }
            }
            return null;
        }
        return null;
    }

    public List<BerTlv> findAll(BerTag aTag) {
        List<BerTlv> list = new ArrayList<BerTlv>();
        if(aTag.equals(getTag())) {
            list.add(this);
            return list;
        } else if(isConstructed()) {
            for (BerTlv tlv : theList) {
                list.addAll(tlv.findAll(aTag));
            }
        }
        return list;
    }

    //
    // getters
    //

    public String getHexValue() {
        if(isConstructed()) throw new IllegalStateException("Tag is CONSTRUCTED "+ HexUtil.toHexString(theTag.bytes));
        return HexUtil.toHexString(theValue);
    }

    /**
     * Text value with US-ASCII charset
     * @return text
     */
    public String getTextValue() {
        return getTextValue(ASCII);
    }

    public String getTextValue(Charset aCharset) {
        if(isConstructed()) {
            throw new IllegalStateException("TLV is constructed");
        }
        return new String(theValue, aCharset);
    }

    public byte[] getBytesValue() {
        if(isConstructed()) {
            throw new IllegalStateException("TLV ["+theTag+"]is constructed");
        }
        return theValue;
    }

    public int getIntValue() {
        int i=0;
        int j=0;
        int number = 0;

        for (i = 0; i < theValue.length; i++) {
            j=theValue[i];
            number = number * 256 + ( j<0 ? j+=256 : j);
        }
        return number;
    }

    public List<BerTlv> getValues() {
        if(isPrimitive()) throw  new IllegalStateException("Tag is PRIMITIVE");
        return theList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BerTlv berTlv = (BerTlv) o;

        if (theTag != null ? !theTag.equals(berTlv.theTag) : berTlv.theTag != null) return false;
        if (!Arrays.equals(theValue, berTlv.theValue)) return false;
        return theList != null ? theList.equals(berTlv.theList) : berTlv.theList == null;
    }

    @Override
    public int hashCode() {
        int result = theTag != null ? theTag.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(theValue);
        result = 31 * result + (theList != null ? theList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        return "BerTlv{" +
                "theTag=" + theTag +
                ", theValue=" + Arrays.toString(theValue) +
                ", theList=" + theList +
                '}';
    }

}
