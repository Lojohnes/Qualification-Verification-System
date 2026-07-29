package com.aqvp.platform.identity.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.ApiClient;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Repository tests for {@link ApiClient} persistence.
 */
@DataJpaTest
@ActiveProfiles("test")
class ApiClientRepositoryTest {

    @Autowired
    private ApiClientRepository apiClientRepository;

    @Test
    void shouldSaveAndFindByClientId() {
        final ApiClient client = ApiClient.builder()
            .clientId("test-client")
            .clientSecretHash("hashed")
            .name("Test Client")
            .authorities(Set.of("role:read"))
            .build();
        apiClientRepository.save(client);

        final Optional<ApiClient> found = apiClientRepository.findByClientId("test-client");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Client");
        assertThat(found.get().getAuthorities()).contains("role:read");
    }

    @Test
    void shouldCheckExistenceByClientId() {
        final ApiClient client = ApiClient.builder()
            .clientId("existing-client")
            .clientSecretHash("hashed")
            .name("Existing")
            .build();
        apiClientRepository.save(client);

        assertThat(apiClientRepository.existsByClientId("existing-client")).isTrue();
        assertThat(apiClientRepository.existsByClientId("missing-client")).isFalse();
    }
}
