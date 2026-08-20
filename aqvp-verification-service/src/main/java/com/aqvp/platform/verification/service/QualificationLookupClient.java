package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.dto.QualificationVerificationSnapshotDto;

/**
 * Client for authoritative qualification verification snapshots.
 */
public interface QualificationLookupClient {

    QualificationVerificationSnapshotDto findBySecurityIdentifier(String securityIdentifier);
}
