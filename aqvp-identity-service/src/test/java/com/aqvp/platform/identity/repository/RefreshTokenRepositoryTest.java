package com.aqvp.platform.identity.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.RefreshToken;
import com.aqvp.platform.identity.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

/**
 * Repository tests for {@link RefreshToken} persistence.
 */
@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndFindTokenByValue() {
        final User user = persistUser("tokenuser");
        final RefreshToken token = RefreshToken.builder()
            .token("token-value")
            .expiryDate(LocalDateTime.now().plusDays(7))
            .revoked(false)
            .user(user)
            .build();
        refreshTokenRepository.save(token);

        final Optional<RefreshToken> found = refreshTokenRepository.findByToken("token-value");

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getUsername()).isEqualTo("tokenuser");
    }

    @Test
    void shouldFindActiveTokensByUser() {
        final User user = persistUser("activeuser");
        final RefreshToken active = RefreshToken.builder()
            .token("active-token")
            .expiryDate(LocalDateTime.now().plusDays(7))
            .revoked(false)
            .user(user)
            .build();
        final RefreshToken revoked = RefreshToken.builder()
            .token("revoked-token")
            .expiryDate(LocalDateTime.now().plusDays(7))
            .revoked(true)
            .user(user)
            .build();
        refreshTokenRepository.save(active);
        refreshTokenRepository.save(revoked);

        final List<RefreshToken> result = refreshTokenRepository.findByUserAndRevokedFalse(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToken()).isEqualTo("active-token");
    }

    private User persistUser(final String username) {
        final User user = User.builder()
            .username(username)
            .email(username + "@aqvp.local")
            .password("encoded")
            .build();
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }
}
