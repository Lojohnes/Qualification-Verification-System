package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.dto.UserRequestDto;
import com.aqvp.platform.identity.dto.UserResponseDto;
import com.aqvp.platform.identity.dto.UserUpdateRequestDto;
import java.util.List;
import java.util.UUID;

/**
 * Service contract for managing users and their role assignments.
 */
public interface UserService {

    UserResponseDto createUser(UserRequestDto dto);

    UserResponseDto updateUser(UUID id, UserUpdateRequestDto dto);

    void deleteUser(UUID id);

    UserResponseDto findById(UUID id);

    List<UserResponseDto> findAll();
}
