package com.aqvp.platform.qualification.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Stored document bytes and download metadata.
 */
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification = "Record constructor/accessor defensively copy the document byte array."
)
public record DocumentFileDto(
    String fileName,
    String contentType,
    byte[] content
) {

    public DocumentFileDto {
        content = content != null ? content.clone() : new byte[0];
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
