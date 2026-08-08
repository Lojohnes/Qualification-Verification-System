package com.aqvp.platform.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for {@link ApiClientServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ApiClientServiceTest {

    @Mock
    private ApiClientRepository apiClientRepository;

    @Mock
    private ApiClientMapper apiClientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ApiClientServiceImpl apiClientService;

    @Test
    void shouldCreateApiClient() {
        final UUID id = UUID.randomUUID();
        final ApiClientRequestDto request = new ApiClientRequestDto(
            "client-1", "secret", "Test Client", "description", true, Set.of("role:read")
        );
        final ApiClient entity = ApiClient.builder().clientId(request.clientId()).name(request.name()).build();
        final ApiClient saved = ApiClient.builder().id(id).clientId(request.clientId()).name(request.name()).build();

        when(apiClientRepository.existsByClientId(request.clientId())).thenReturn(false);
        when(apiClientMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode(request.clientSecret())).thenReturn("hashed");
        when(apiClientRepository.save(entity)).thenReturn(saved);
        when(apiClientMapper.toResponseDto(saved)).thenReturn(
            new ApiClientResponseDto(id, saved.getClientId(), saved.getName(), null, true, Set.of("role:read"))
        );

        final ApiClientResponseDto response = apiClientService.createApiClient(request);

        assertThat(response.clientId()).isEqualTo("client-1");
        assertThat(entity.getClientSecretHash()).isEqualTo("hashed");
        verify(apiClientRepository).save(entity);
    }

    @Test
    void shouldThrowWhenCreatingDuplicateApiClient() {
        final ApiClientRequestDto request = new ApiClientRequestDto(
            "client-1", "secret", "Test", null, true, Set.of()
        );
        when(apiClientRepository.existsByClientId(request.clientId())).thenReturn(true);

        assertThatThrownBy(() -> apiClientService.createApiClient(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void shouldUpdateApiClient() {
        final UUID id = UUID.randomUUID();
        final ApiClient existing = ApiClient.builder().id(id).clientId("old").build();
        final ApiClientRequestDto request = new ApiClientRequestDto(
            "client-1", "new-secret", "Updated", null, true, Set.of()
        );
        final ApiClient saved = ApiClient.builder().id(id).clientId("client-1").name("Updated").build();

        when(apiClientRepository.findById(id)).thenReturn(Optional.of(existing));
        when(apiClientRepository.save(existing)).thenReturn(saved);
        when(passwordEncoder.encode(request.clientSecret())).thenReturn("hashed");
        when(apiClientMapper.toResponseDto(saved)).thenReturn(
            new ApiClientResponseDto(id, "client-1", "Updated", null, true, Set.of())
        );

        final ApiClientResponseDto response = apiClientService.updateApiClient(id, request);

        assertThat(response.name()).isEqualTo("Updated");
        assertThat(existing.getClientSecretHash()).isEqualTo("hashed");
    }

    @Test
    void shouldThrowWhenUpdatingMissingApiClient() {
        final UUID id = UUID.randomUUID();
        when(apiClientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> apiClientService.updateApiClient(id, new ApiClientRequestDto(
            "x", "s", "n", null, true, Set.of()
        )))
            .isInstanceOf(ApiClientNotFoundException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void shouldFindApiClientById() {
        final UUID id = UUID.randomUUID();
        final ApiClient client = ApiClient.builder().id(id).clientId("client-1").build();

        when(apiClientRepository.findById(id)).thenReturn(Optional.of(client));
        when(apiClientMapper.toResponseDto(client)).thenReturn(
            new ApiClientResponseDto(id, "client-1", null, null, true, Set.of())
        );

        final ApiClientResponseDto response = apiClientService.findById(id);

        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    void shouldThrowWhenApiClientNotFoundById() {
        final UUID id = UUID.randomUUID();
        when(apiClientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> apiClientService.findById(id))
            .isInstanceOf(ApiClientNotFoundException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void shouldFindAllApiClients() {
        final ApiClient client = ApiClient.builder().id(UUID.randomUUID()).clientId("client-1").build();
        when(apiClientRepository.findAll()).thenReturn(List.of(client));
        when(apiClientMapper.toResponseDto(client)).thenReturn(
            new ApiClientResponseDto(client.getId(), "client-1", null, null, true, Set.of())
        );

        final List<ApiClientResponseDto> result = apiClientService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldAuthenticateApiClient() {
        final ApiClient client = ApiClient.builder()
            .id(UUID.randomUUID())
            .clientId("client-1")
            .clientSecretHash("hashed")
            .enabled(true)
            .authorities(Set.of("role:read"))
            .build();

        when(apiClientRepository.findByClientId("client-1")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);

        final Authentication authentication = apiClientService.authenticate("client-1", "secret");

        assertThat(authentication).isInstanceOf(ApiClientAuthenticationToken.class);
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getAuthorities()).hasSize(1);
    }

    @Test
    void shouldThrowWhenAuthenticatingDisabledApiClient() {
        final ApiClient client = ApiClient.builder()
            .clientId("client-1")
            .clientSecretHash("hashed")
            .enabled(false)
            .build();

        when(apiClientRepository.findByClientId("client-1")).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> apiClientService.authenticate("client-1", "secret"))
            .isInstanceOf(ApiClientDisabledException.class)
            .hasMessageContaining("disabled");
    }

    @Test
    void shouldThrowWhenApiClientSecretDoesNotMatch() {
        final ApiClient client = ApiClient.builder()
            .clientId("client-1")
            .clientSecretHash("hashed")
            .enabled(true)
            .build();

        when(apiClientRepository.findByClientId("client-1")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> apiClientService.authenticate("client-1", "wrong"))
            .isInstanceOf(InvalidCredentialsException.class)
            .hasMessageContaining("Invalid API client credentials");
    }

    @Test
    void shouldThrowWhenAuthenticatingUnknownApiClient() {
        when(apiClientRepository.findByClientId("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> apiClientService.authenticate("unknown", "secret"))
            .isInstanceOf(ApiClientNotFoundException.class)
            .hasMessageContaining("not found");
    }
}
