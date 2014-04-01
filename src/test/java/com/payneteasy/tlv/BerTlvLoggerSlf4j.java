package com.payneteasy.tlv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BerTlvLoggerSlf4j implements IBerTlvLogger {

    private final static Logger LOG = LoggerFactory.getLogger(BerTlvLoggerSlf4j.class);

    public boolean isDebugEnabled() {
        return LOG.isDebugEnabled();
    }

    public void debug(String aFormat, Object... args) {
        LOG.debug(aFormat, args);
    }
}
