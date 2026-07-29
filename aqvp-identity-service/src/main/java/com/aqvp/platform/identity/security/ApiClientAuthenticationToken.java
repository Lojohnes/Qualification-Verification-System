package com.aqvp.platform.identity.security;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Authentication token used for API client key/secret authentication.
 */
public final class ApiClientAuthenticationToken extends AbstractAuthenticationToken {

    private final String clientId;
    private final String clientSecret;

    /**
     * Creates an unauthenticated API client token.
     *
     * @param clientId     the client identifier
     * @param clientSecret the client secret
     */
    public ApiClientAuthenticationToken(String clientId, String clientSecret) {
        super(null);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        setAuthenticated(false);
    }

    /**
     * Creates an authenticated API client token with granted authorities.
     *
     * @param clientId     the client identifier
     * @param clientSecret the client secret
     * @param authorities  the granted authorities
     */
    public ApiClientAuthenticationToken(String clientId, String clientSecret,
                                        Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return clientSecret;
    }

    @Override
    public Object getPrincipal() {
        return clientId;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor instead");
        }
        super.setAuthenticated(false);
    }
}
