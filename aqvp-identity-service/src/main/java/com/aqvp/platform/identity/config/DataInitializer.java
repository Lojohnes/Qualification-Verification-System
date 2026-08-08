package com.aqvp.platform.identity.config;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.domain.Role;
import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.repository.PermissionRepository;
import com.aqvp.platform.identity.repository.RoleRepository;
import com.aqvp.platform.identity.repository.UserRepository;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the local H2 database with the default admin user, roles and permissions.
 */
@Component
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        final Map<String, Permission> permissions = seedPermissions();
        final Role adminRole = seedAdminRole(permissions.values());
        seedAdminUser(adminRole);
    }

    private Map<String, Permission> seedPermissions() {
        final Map<String, Permission> existing = permissionRepository.findAll().stream()
            .collect(Collectors.toMap(Permission::getName, Function.identity()));

        final Permission[] defaults = {
            createPermission("user:read", "user", "read", "Read users"),
            createPermission("user:write", "user", "write", "Create or update users"),
            createPermission("user:delete", "user", "delete", "Delete users"),
            createPermission("role:read", "role", "read", "Read roles and permissions"),
            createPermission("role:write", "role", "write", "Create or update roles")
        };

        for (Permission permission : defaults) {
            if (!existing.containsKey(permission.getName())) {
                existing.put(permission.getName(), permissionRepository.save(permission));
                log.info("Created permission '{}'", permission.getName());
            }
        }

        return existing;
    }

    private Permission createPermission(String name, String resource, String action, String description) {
        final Permission permission = new Permission();
        permission.setName(name);
        permission.setResource(resource);
        permission.setAction(action);
        permission.setDescription(description);
        return permission;
    }

    private Role seedAdminRole(final java.util.Collection<Permission> permissions) {
        return roleRepository.findByName("ADMIN").orElseGet(() -> {
            final Role role = new Role();
            role.setName("ADMIN");
            role.setDescription("Administrator with full access");
            role.setPermissions(new HashSet<>(permissions));
            final Role saved = roleRepository.save(role);
            log.info("Created role '{}'", saved.getName());
            return saved;
        });
    }

    private void seedAdminUser(final Role adminRole) {
        if (userRepository.existsByUsername("admin")) {
            log.info("Admin user already exists");
            return;
        }

        final User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@aqvp.local");
        admin.setPassword(passwordEncoder.encode("Admin123!"));
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setEnabled(true);
        admin.setAccountNonExpired(true);
        admin.setAccountNonLocked(true);
        admin.setCredentialsNonExpired(true);
        admin.setEmailVerified(true);
        admin.setMfaEnabled(false);
        admin.setRoles(new HashSet<>(Set.of(adminRole)));
        userRepository.save(admin);
        log.info("Created admin user 'admin'");
    }
}
