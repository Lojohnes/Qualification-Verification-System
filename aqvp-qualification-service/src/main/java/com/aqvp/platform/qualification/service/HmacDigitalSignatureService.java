package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.config.DocumentProperties;
import com.aqvp.platform.qualification.exception.BusinessException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * HMAC-based detached signature implementation for generated document artifacts.
 */
@Service
@RequiredArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed configuration bean injection")
public class HmacDigitalSignatureService implements DigitalSignatureService {

    private static final String ALGORITHM = "HmacSHA256";

    private final DocumentProperties documentProperties;

    @Override
    public String algorithm() {
        return ALGORITHM;
    }

    @Override
    public String signerKeyId() {
        return documentProperties.getSignerKeyId();
    }

    @Override
    public String sign(String payload, byte[] content) {
        try {
            final Mac mac = Mac.getInstance(ALGORITHM);
            final SecretKeySpec key = new SecretKeySpec(
                    documentProperties.getSigningSecret().getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(key);
            mac.update(payload.getBytes(StandardCharsets.UTF_8));
            mac.update((byte) ':');
            mac.update(content);
            return Base64.getEncoder().encodeToString(mac.doFinal());
        } catch (Exception ex) {
            throw new BusinessException("Unable to sign generated document", ex);
        }
    }
}
