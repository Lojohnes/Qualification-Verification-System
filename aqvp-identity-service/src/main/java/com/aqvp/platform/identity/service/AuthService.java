package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.dto.AuthenticationRequest;
import com.aqvp.platform.identity.dto.AuthenticationResponse;
import com.aqvp.platform.identity.dto.ChangePasswordRequest;
import com.aqvp.platform.identity.dto.ForgotPasswordRequest;
import com.aqvp.platform.identity.dto.RefreshTokenRequest;
import com.aqvp.platform.identity.dto.ResetPasswordRequest;
import com.aqvp.platform.identity.dto.UserResponseDto;

/**
 * Service contract for authentication and password management operations.
 */
public interface AuthService {

    AuthenticationResponse login(AuthenticationRequest dto);

    void logout(RefreshTokenRequest dto);

    AuthenticationResponse refresh(RefreshTokenRequest dto);

    void changePassword(ChangePasswordRequest dto);

    void forgotPassword(ForgotPasswordRequest dto);

    void resetPassword(ResetPasswordRequest dto);

    UserResponseDto getCurrentUser();
}
