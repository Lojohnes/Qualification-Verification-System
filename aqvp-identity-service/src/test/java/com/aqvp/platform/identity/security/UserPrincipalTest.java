package com.aqvp.platform.identity.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.domain.Role;
import com.aqvp.platform.identity.domain.User;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UserPrincipal}.
 */
class UserPrincipalTest {

    @Test
    void shouldBuildPrincipalFromUser() {
        final Permission permission = Permission.builder().name("user:read").resource("user").action("read").build();
        final Role role = Role.builder().name("USER").permissions(Set.of(permission)).build();
        final User user = User.builder()
            .username("johndoe")
            .password("encoded")
            .enabled(true)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .roles(Set.of(role))
            .build();

        final UserPrincipal principal = UserPrincipal.of(user);

        assertThat(principal.getUsername()).isEqualTo("johndoe");
        assertThat(principal.getPassword()).isEqualTo("encoded");
        assertThat(principal.getAuthorities()).hasSize(1);
        assertThat(principal.getAuthorities().iterator().next().getAuthority()).isEqualTo("user:read");
        assertThat(principal.isEnabled()).isTrue();
        assertThat(principal.isAccountNonExpired()).isTrue();
        assertThat(principal.isAccountNonLocked()).isTrue();
        assertThat(principal.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenUserHasNoRoles() {
        final User user = User.builder().username("norole").password("encoded").build();

        final UserPrincipal principal = UserPrincipal.of(user);

        assertThat(principal.getAuthorities()).isEmpty();
    }
}
