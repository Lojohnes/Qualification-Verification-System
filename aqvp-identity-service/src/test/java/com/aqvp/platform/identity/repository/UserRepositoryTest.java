package com.aqvp.platform.identity.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Repository tests for {@link User} persistence.
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByUsername() {
        final User user = User.builder()
            .username("johndoe")
            .email("john@aqvp.local")
            .password("encoded")
            .build();

        userRepository.save(user);
        final Optional<User> found = userRepository.findByUsername("johndoe");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@aqvp.local");
        assertThat(found.get().getId()).isNotNull();
    }

    @Test
    void shouldFindUserByUsernameOrEmail() {
        final User user = User.builder()
            .username("janedoe")
            .email("jane@aqvp.local")
            .password("encoded")
            .build();

        userRepository.save(user);

        assertThat(userRepository.findByUsernameOrEmail("janedoe", "unknown")).isPresent();
        assertThat(userRepository.findByUsernameOrEmail("unknown", "jane@aqvp.local")).isPresent();
    }

    @Test
    void shouldCheckExistenceByUsernameAndEmail() {
        final User user = User.builder()
            .username("exists")
            .email("exists@aqvp.local")
            .password("encoded")
            .build();

        userRepository.save(user);

        assertThat(userRepository.existsByUsername("exists")).isTrue();
        assertThat(userRepository.existsByEmail("exists@aqvp.local")).isTrue();
        assertThat(userRepository.existsByUsername("missing")).isFalse();
    }
}
