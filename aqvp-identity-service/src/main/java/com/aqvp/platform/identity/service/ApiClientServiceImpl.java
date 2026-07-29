package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.domain.ApiClient;
import com.aqvp.platform.identity.dto.ApiClientRequestDto;
import com.aqvp.platform.identity.dto.ApiClientResponseDto;
import com.aqvp.platform.identity.exception.ApiClientDisabledException;
import com.aqvp.platform.identity.exception.ApiClientNotFoundException;
import com.aqvp.platform.identity.exception.InvalidCredentialsException;
import com.aqvp.platform.identity.mapper.ApiClientMapper;
import com.aqvp.platform.identity.repository.ApiClientRepository;
import com.aqvp.platform.identity.security.ApiClientAuthenticationToken;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements API client credential management and authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiClientServiceImpl implements ApiClientService {

    private final ApiClientRepository apiClientRepository;
    private final ApiClientMapper apiClientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('role:write')")
    public ApiClientResponseDto createApiClient(ApiClientRequestDto dto) {
        if (apiClientRepository.existsByClientId(dto.clientId())) {
            throw new IllegalArgumentException("API client already exists: " + dto.clientId());
        }
        final ApiClient client = apiClientMapper.toEntity(dto);
        client.setClientSecretHash(passwordEncoder.encode(dto.clientSecret()));
        final ApiClient saved = apiClientRepository.save(client);
        log.info("Created API client '{}' with id {}", saved.getClientId(), saved.getId());
        return apiClientMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('role:write')")
    public ApiClientResponseDto updateApiClient(UUID id, ApiClientRequestDto dto) {
        final ApiClient client = apiClientRepository.findById(id)
            .orElseThrow(() -> new ApiClientNotFoundException("API client not found: " + id));
        apiClientMapper.updateEntity(dto, client);
        if (dto.clientSecret() != null && !dto.clientSecret().isBlank()) {
            client.setClientSecretHash(passwordEncoder.encode(dto.clientSecret()));
        }
        final ApiClient saved = apiClientRepository.save(client);
        log.info("Updated API client '{}' with id {}", saved.getClientId(), saved.getId());
        return apiClientMapper.toResponseDto(saved);
    }

    @Override
    @PreAuthorize("hasAuthority('role:read')")
    public ApiClientResponseDto findById(UUID id) {
        return apiClientRepository.findById(id)
            .map(apiClientMapper::toResponseDto)
            .orElseThrow(() -> new ApiClientNotFoundException("API client not found: " + id));
    }

    @Override
    @PreAuthorize("hasAuthority('role:read')")
    public List<ApiClientResponseDto> findAll() {
        return apiClientRepository.findAll().stream()
            .map(apiClientMapper::toResponseDto)
            .toList();
    }

    @Override
    public Authentication authenticate(String clientId, String clientSecret) {
        final ApiClient client = apiClientRepository.findByClientId(clientId)
            .orElseThrow(() -> new ApiClientNotFoundException("API client not found"));

        if (!client.getEnabled()) {
            throw new ApiClientDisabledException("API client is disabled");
        }

        if (!passwordEncoder.matches(clientSecret, client.getClientSecretHash())) {
            throw new InvalidCredentialsException("Invalid API client credentials");
        }

        log.info("Authenticated API client '{}'", clientId);
        return new ApiClientAuthenticationToken(
            clientId,
            null,
            client.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet())
        );
    }
}
