package com.aqvp.platform.identity.security;

import com.aqvp.platform.identity.domain.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security user details wrapper around the {@link User} aggregate.
 */
@Getter
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "SE_BAD_FIELD"},
    justification = "UserPrincipal is not serialized; the wrapped User is intentionally exposed as read-only."
)
public class UserPrincipal implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final transient User user;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    private UserPrincipal(User user, String username, String password,
                          Collection<? extends GrantedAuthority> authorities,
                          boolean accountNonExpired, boolean accountNonLocked,
                          boolean credentialsNonExpired, boolean enabled) {
        this.user = user;
        this.username = username;
        this.password = password;
        this.authorities = Collections.unmodifiableCollection(authorities);
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    /**
     * Builds a principal from a domain user, flattening role permissions into authorities.
     *
     * @param user the domain user
     * @return a fully configured {@link UserPrincipal}
     */
    public static UserPrincipal of(User user) {
        final Collection<GrantedAuthority> authorities = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
            .distinct()
            .collect(Collectors.toSet());

        return new UserPrincipal(
            user,
            user.getUsername(),
            user.getPassword(),
            authorities,
            user.getAccountNonExpired(),
            user.getAccountNonLocked(),
            user.getCredentialsNonExpired(),
            user.getEnabled()
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
