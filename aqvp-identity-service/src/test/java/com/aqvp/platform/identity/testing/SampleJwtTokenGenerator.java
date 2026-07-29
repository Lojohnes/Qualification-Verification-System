package com.aqvp.platform.identity.testing;

import static org.mockito.Mockito.when;

import com.aqvp.platform.identity.config.JwtConfig;
import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.domain.Role;
import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.security.JwtService;
import com.aqvp.platform.identity.security.UserPrincipal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Utility test that generates sample JWT access tokens for the documented test users.
 * Run manually with:
 * mvn -pl aqvp-identity-service -Dtest=SampleJwtTokenGenerator test
 */
@Disabled("Run manually to regenerate sample JWT tokens")
@ExtendWith(MockitoExtension.class)
class SampleJwtTokenGenerator {

    private static final String SECRET = "change-me-very-long-secret-key-at-least-256-bits";

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void generateSampleTokens() throws IOException {
        when(jwtConfig.secret()).thenReturn(SECRET);
        when(jwtConfig.accessTokenExpirationMs()).thenReturn(900000L);

        final List<TestUser> users = List.of(
            new TestUser("system_admin", allPermissions()),
            new TestUser("institution_admin", Set.of("user:read", "user:write", "role:read")),
            new TestUser("registrar", Set.of("user:read", "user:write")),
            new TestUser("verifier", Set.of("user:read")),
            new TestUser("auditor", Set.of("user:read", "role:read"))
        );

        final StringBuilder output = new StringBuilder("# Sample JWT Access Tokens\n\n");
        output.append("Generated with secret: `").append(SECRET).append("`\n\n");
        output.append("| User | Authorities | Token |\n");
        output.append("|------|-------------|-------|\n");

        for (final TestUser testUser : users) {
            final User user = buildUser(testUser.username, testUser.authorities);
            final String token = jwtService.generateAccessToken(UserPrincipal.of(user));
            output.append("| ").append(testUser.username)
                .append(" | ").append(String.join(", ", testUser.authorities))
                .append(" | `").append(token).append("` |\n");
        }

        final Path path = Paths.get("src/test/resources/testing/jwt/sample-jwt-tokens.md");
        Files.createDirectories(path.getParent());
        Files.writeString(path, output.toString());
    }

    private User buildUser(final String username, final Set<String> authorities) {
        final Set<Permission> permissions = authorities.stream()
            .map(name -> Permission.builder()
                .name(name)
                .resource(name.split(":")[0])
                .action(name.split(":")[1])
                .build())
            .collect(java.util.stream.Collectors.toSet());
        final Role role = Role.builder()
            .id(UUID.randomUUID())
            .name(username.toUpperCase())
            .permissions(permissions)
            .build();
        return User.builder()
            .id(UUID.randomUUID())
            .username(username)
            .email(username + "@aqvp.local")
            .password("encoded")
            .roles(Set.of(role))
            .build();
    }

    private Set<String> allPermissions() {
        return Set.of("user:read", "user:write", "user:delete", "role:read", "role:write");
    }

    private record TestUser(String username, Set<String> authorities) {
    }
}
