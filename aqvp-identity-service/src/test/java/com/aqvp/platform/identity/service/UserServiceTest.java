package com.aqvp.platform.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.identity.domain.Role;
import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.dto.UserRequestDto;
import com.aqvp.platform.identity.dto.UserResponseDto;
import com.aqvp.platform.identity.mapper.UserMapper;
import com.aqvp.platform.identity.repository.RoleRepository;
import com.aqvp.platform.identity.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldCreateUserAndReturnResponse() {
        final UUID roleId = UUID.randomUUID();
        final Role role = Role.builder().id(roleId).name("USER").build();
        final UserRequestDto request = new UserRequestDto(
            "johndoe",
            "john@aqvp.local",
            "Password123!",
            "John",
            "Doe",
            Set.of(roleId)
        );

        final User userEntity = User.builder().username(request.username()).email(request.email()).build();
        final User savedUser = User.builder()
            .id(UUID.randomUUID())
            .username(request.username())
            .email(request.email())
            .roles(Set.of(role))
            .build();

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(roleRepository.findAllById(request.roleIds())).thenReturn(List.of(role));
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(userRepository.save(userEntity)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(
            new UserResponseDto(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(),
                "John", "Doe", true, true, false, Set.of("USER"), Set.of())
        );

        final UserResponseDto response = userService.createUser(request);

        assertThat(response.username()).isEqualTo("johndoe");
        assertThat(response.email()).isEqualTo("john@aqvp.local");
        assertThat(response.roles()).contains("USER");
        verify(userRepository).save(userEntity);
    }

    @Test
    void shouldFindAllUsers() {
        final User user = User.builder()
            .id(UUID.randomUUID())
            .username("johndoe")
            .email("john@aqvp.local")
            .build();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(
            new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(),
                null, null, true, false, false, Set.of(), Set.of())
        );

        final List<UserResponseDto> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("johndoe");
    }

    @Test
    void shouldDeleteUser() {
        final UUID id = UUID.randomUUID();
        final User user = User.builder().id(id).username("delete").build();
        when(userRepository.findById(id)).thenReturn(java.util.Optional.of(user));

        userService.deleteUser(id);

        verify(userRepository).delete(user);
    }
}
