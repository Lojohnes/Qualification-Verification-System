package com.aqvp.platform.qualification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for document storage and signing.
 */
@Component
@ConfigurationProperties(prefix = "aqvp.documents")
public class DocumentProperties {

    private String storageRoot = "data/documents";
    private String signingSecret = "change-me-document-signing-secret-at-least-256-bits";
    private String signerKeyId = "local-dev-key";

    public String getStorageRoot() {
        return storageRoot;
    }

    public void setStorageRoot(String storageRoot) {
        this.storageRoot = storageRoot;
    }

    public String getSigningSecret() {
        return signingSecret;
    }

    public void setSigningSecret(String signingSecret) {
        this.signingSecret = signingSecret;
    }

    public String getSignerKeyId() {
        return signerKeyId;
    }

    public void setSignerKeyId(String signerKeyId) {
        this.signerKeyId = signerKeyId;
    }
}
