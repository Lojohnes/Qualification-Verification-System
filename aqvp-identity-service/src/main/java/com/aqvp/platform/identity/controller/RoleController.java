package com.aqvp.platform.identity.controller;

import com.aqvp.platform.identity.dto.RoleRequestDto;
import com.aqvp.platform.identity.dto.RoleResponseDto;
import com.aqvp.platform.identity.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST endpoints for role management.
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Role Management", description = "Create and query roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "List all roles")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a role by id")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new role")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto request) {
        final RoleResponseDto created = roleService.createRole(request);
        final URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing role")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable UUID id,
                                                      @Valid @RequestBody RoleRequestDto request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }
}
