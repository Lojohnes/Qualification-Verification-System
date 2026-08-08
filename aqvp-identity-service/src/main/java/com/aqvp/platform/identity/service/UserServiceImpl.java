package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.domain.Role;
import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.dto.UserRequestDto;
import com.aqvp.platform.identity.dto.UserResponseDto;
import com.aqvp.platform.identity.dto.UserUpdateRequestDto;
import com.aqvp.platform.identity.exception.EmailAlreadyExistsException;
import com.aqvp.platform.identity.exception.EntityNotFoundException;
import com.aqvp.platform.identity.exception.UsernameAlreadyExistsException;
import com.aqvp.platform.identity.mapper.UserMapper;
import com.aqvp.platform.identity.repository.RoleRepository;
import com.aqvp.platform.identity.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements user management with role assignment and audit logging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('user:write')")
    public UserResponseDto createUser(UserRequestDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new UsernameAlreadyExistsException("Username already taken: " + dto.username());
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email already registered: " + dto.email());
        }

        final User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(resolveRoles(dto.roleIds()));

        final User saved = userRepository.save(user);
        log.info("Created user '{}' with id {}", saved.getUsername(), saved.getId());
        return userMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('user:write')")
    public UserResponseDto updateUser(UUID id, UserUpdateRequestDto dto) {
        final User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        userMapper.updateEntity(dto, user);
        if (dto.roleIds() != null && !dto.roleIds().isEmpty()) {
            user.setRoles(resolveRoles(dto.roleIds()));
        }

        final User saved = userRepository.save(user);
        log.info("Updated user '{}' with id {}", saved.getUsername(), saved.getId());
        return userMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('user:delete')")
    public void deleteUser(UUID id) {
        final User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        userRepository.delete(user);
        log.info("Deleted user '{}' with id {}", user.getUsername(), user.getId());
    }

    @Override
    @PreAuthorize("hasAuthority('user:read')")
    public UserResponseDto findById(UUID id) {
        return userRepository.findById(id)
            .map(userMapper::toResponseDto)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    @Override
    @PreAuthorize("hasAuthority('user:read')")
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
            .map(userMapper::toResponseDto)
            .toList();
    }

    private Set<Role> resolveRoles(Set<UUID> roleIds) {
        final Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            final Set<UUID> missing = roleIds.stream()
                .filter(id -> roles.stream().noneMatch(role -> role.getId().equals(id)))
                .collect(Collectors.toSet());
            throw new EntityNotFoundException("Roles not found: " + missing);
        }
        return roles;
    }
}
