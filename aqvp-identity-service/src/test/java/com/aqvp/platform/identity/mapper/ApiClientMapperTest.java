package com.aqvp.platform.identity.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.ApiClient;
import com.aqvp.platform.identity.dto.ApiClientRequestDto;
import com.aqvp.platform.identity.dto.ApiClientResponseDto;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ApiClientMapper}.
 */
class ApiClientMapperTest {

    private final ApiClientMapper mapper = new ApiClientMapperImpl();

    @Test
    void shouldMapApiClientRequestDtoToEntity() {
        final ApiClientRequestDto dto = new ApiClientRequestDto(
            "client-1", "secret", "Test", "Description", true, Set.of("role:read")
        );

        final ApiClient entity = mapper.toEntity(dto);

        assertThat(entity.getClientId()).isEqualTo("client-1");
        assertThat(entity.getName()).isEqualTo("Test");
        assertThat(entity.getDescription()).isEqualTo("Description");
        assertThat(entity.getEnabled()).isTrue();
        assertThat(entity.getAuthorities()).contains("role:read");
        assertThat(entity.getClientSecretHash()).isNull();
    }

    @Test
    void shouldMapApiClientToResponseDto() {
        final ApiClient entity = ApiClient.builder()
            .id(UUID.randomUUID())
            .clientId("client-1")
            .name("Test")
            .description("Description")
            .enabled(true)
            .authorities(Set.of("role:read"))
            .build();

        final ApiClientResponseDto dto = mapper.toResponseDto(entity);

        assertThat(dto.clientId()).isEqualTo("client-1");
        assertThat(dto.authorities()).contains("role:read");
    }

    @Test
    void shouldUpdateEntityFromDto() {
        final ApiClient entity = ApiClient.builder().clientId("old").name("Old").build();
        final ApiClientRequestDto dto = new ApiClientRequestDto(
            "new", "secret", "New", "Updated", false, Set.of()
        );

        mapper.updateEntity(dto, entity);

        assertThat(entity.getClientId()).isEqualTo("new");
        assertThat(entity.getName()).isEqualTo("New");
        assertThat(entity.getDescription()).isEqualTo("Updated");
        assertThat(entity.getEnabled()).isFalse();
    }
}
