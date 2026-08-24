package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.dto.QualificationVerificationSnapshotDto;

/**
 * Read-only internal service exposing qualification snapshots for verification.
 */
public interface QualificationVerificationService {

    QualificationVerificationSnapshotDto getBySecurityIdentifier(String securityIdentifier);

    QualificationVerificationSnapshotDto getByQualificationNumber(String qualificationNumber);
}
