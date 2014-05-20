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

    public List<BerTlv> findAll(BerTag aTag) {
        List<BerTlv> list = new ArrayList<BerTlv>();
        for (BerTlv tlv : tlvs) {
            list.addAll(tlv.findAll(aTag));
        }
        return list;
    }

    private final List<BerTlv> tlvs = new ArrayList<BerTlv>();
}
