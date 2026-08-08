package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.dto.ApiClientRequestDto;
import com.aqvp.platform.identity.dto.ApiClientResponseDto;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;

/**
 * Service contract for API client credential management and authentication.
 */
public interface ApiClientService {

    ApiClientResponseDto createApiClient(ApiClientRequestDto dto);

    ApiClientResponseDto updateApiClient(UUID id, ApiClientRequestDto dto);

    ApiClientResponseDto findById(UUID id);

    List<ApiClientResponseDto> findAll();

    Authentication authenticate(String clientId, String clientSecret);
}
