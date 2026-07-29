package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.domain.RefreshToken;
import com.aqvp.platform.identity.domain.User;

/**
 * Service contract for refresh token lifecycle management.
 */
public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(String token);

    void revokeByToken(String token);

    void revokeAllByUser(User user);
}
