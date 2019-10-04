package com.payneteasy.tlv;

public interface BerTagFactory {
	BerTag createTag(byte[] aBuf, int aOffset, int aLength);
}
