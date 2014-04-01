package com.payneteasy.tlv;

import java.util.ArrayList;
import java.util.List;

public class BerTlvs {

    public void add(BerTlv aTlv) {
        tlvs.add(aTlv);
    }

    public List<BerTlv> getList() {
        return tlvs;
    }

    public BerTlv find(BerTag aTag) {
        for (BerTlv tlv : tlvs) {
            BerTlv found = tlv.find(aTag);
            if(found!=null) {
                return found;
            }
        }
        return null;
    }


    private final List<BerTlv> tlvs = new ArrayList<BerTlv>();
}
