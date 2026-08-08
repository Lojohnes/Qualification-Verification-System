package com.aqvp.platform.identity.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.dto.UserRequestDto;
import com.aqvp.platform.identity.dto.UserResponseDto;
import com.aqvp.platform.identity.dto.UserUpdateRequestDto;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UserMapper}.
 */
class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void shouldMapUserRequestDtoToEntity() {
        final UserRequestDto dto = new UserRequestDto(
            "johndoe", "john@aqvp.local", "Password123!", "John", "Doe", Set.of(UUID.randomUUID())
        );

        final User user = mapper.toEntity(dto);

        assertThat(user.getUsername()).isEqualTo("johndoe");
        assertThat(user.getEmail()).isEqualTo("john@aqvp.local");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getPassword()).isNull();
        assertThat(user.getRoles()).isEmpty();
    }

    @Test
    void shouldMapUserToResponseDto() {
        final User user = User.builder()
            .id(UUID.randomUUID())
            .username("johndoe")
            .email("john@aqvp.local")
            .firstName("John")
            .lastName("Doe")
            .enabled(true)
            .emailVerified(false)
            .mfaEnabled(false)
            .build();

        final UserResponseDto dto = mapper.toResponseDto(user);

        assertThat(dto.username()).isEqualTo("johndoe");
        assertThat(dto.email()).isEqualTo("john@aqvp.local");
        assertThat(dto.roles()).isEmpty();
    }

    @Test
    void shouldUpdateEntityFromDto() {
        final User user = User.builder().username("olduser").email("old@aqvp.local").build();
        final UserUpdateRequestDto dto = new UserUpdateRequestDto(
            "new@aqvp.local", "New", "Name", false, Set.of()
        );

        mapper.updateEntity(dto, user);

        assertThat(user.getEmail()).isEqualTo("new@aqvp.local");
        assertThat(user.getFirstName()).isEqualTo("New");
        assertThat(user.getLastName()).isEqualTo("Name");
        assertThat(user.getEnabled()).isFalse();
        assertThat(user.getUsername()).isEqualTo("olduser");
    }
}
