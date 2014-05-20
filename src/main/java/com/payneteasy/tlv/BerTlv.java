package com.payneteasy.tlv;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class BerTlv {

    private final static Charset UTF8 = Charset.forName("UTF-8");

    public BerTlv(BerTag aTag, List<BerTlv> aList) {
        theTag = aTag;
        theList = aList;
        theValue = null;

    }

    public BerTlv(BerTag aTag, byte[] aValue) {
        theTag = aTag;
        theValue = aValue;
        theList = null;
    }

    public BerTag getTag() {
        return theTag;
    }

    public String getHexValue() {
        if(isConstructed()) throw new IllegalStateException("Tag is CONSTRUCTED "+ HexUtil.toHexString(theTag.bytes));
        return HexUtil.toHexString(theValue);
    }

    public List<BerTlv> getValues() {
        if(isPrimitive()) throw  new IllegalStateException("Tag is PRIMITIVE");
        return theList;
    }

    public boolean isPrimitive() {
        return !theTag.isConstructed();
    }

    public boolean isConstructed() {
        return theTag.isConstructed();
    }

    @Override
    public String toString() {

        return "BerTlv{" +
                "theTag=" + theTag +
                ", theValue=" + Arrays.toString(theValue) +
                ", theList=" + theList +
                '}';
    }

    private final BerTag theTag;
    private final byte[] theValue;
    private List<BerTlv> theList;

    /**
     * Text value with UTF-8 charset
     * @return text
     */
    public String getTextValue() {
        return getTextValue(UTF8);
    }

    public String getTextValue(Charset aCharset) {
        if(isConstructed()) {
            throw new IllegalStateException("TLV is constructed");
        }
        return new String(theValue, aCharset);
    }

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

    public byte[] getBytes() {
        if(isConstructed()) {
            throw new IllegalStateException("TLV is constructed");
        }
        return theValue;
    }

    public int valueAsNumber() {
        int i=0;
        int j=0;
        int number = 0;

        for (i = 0; i < theValue.length; i++) {
            j=theValue[i];
            number = number * 256 + ( j<0 ? j+=256 : j);
        }
        return number;
    }

}
