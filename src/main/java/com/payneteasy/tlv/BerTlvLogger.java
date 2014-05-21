package com.payneteasy.tlv;


public class BerTlvLogger {


    public static void log(String aPadding, BerTlvs aTlv, IBerTlvLogger aLogger) {

        for (BerTlv tlv : aTlv.getList()) {
            log(aPadding, tlv, aLogger);
        }
    }

    public static void log(String aPadding, BerTlv aTlv, IBerTlvLogger aLogger) {
        if (aTlv == null) {
            aLogger.debug("{} is null", aPadding);
            return;
        }

        if (aTlv.isConstructed()) {
            aLogger.debug("{} [{}]", aPadding, HexUtil.toHexString(aTlv.getTag().bytes));
            for (BerTlv child : aTlv.getValues()) {
                log(aPadding + "    ", child, aLogger);
            }
        } else {
            aLogger.debug("{} [{}] {}", aPadding, HexUtil.toHexString(aTlv.getTag().bytes), aTlv.getHexValue());
        }

    }

}
