package com.aqvp.platform.qualification.service;

/**
 * Creates detached digital signatures for generated document payloads.
 */
public interface DigitalSignatureService {

    String algorithm();

    String signerKeyId();

    String sign(String payload, byte[] content);
}
