package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.dto.QualificationVerificationSnapshotDto;
import com.aqvp.platform.verification.exception.UpstreamNotFoundException;
import com.aqvp.platform.verification.exception.UpstreamServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

/**
 * REST implementation of Qualification service snapshot lookups.
 */
@Component
public class RestQualificationLookupClient implements QualificationLookupClient {

    private final RestClient restClient;
    private final String serviceToken;

    public RestQualificationLookupClient(
            RestClient.Builder restClientBuilder,
            @Value("${aqvp.services.qualification.base-url:http://localhost:8082}") String baseUrl,
            @Value("${aqvp.services.qualification.token:}") String serviceToken) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.serviceToken = serviceToken;
    }

    @Override
    public QualificationVerificationSnapshotDto findBySecurityIdentifier(String securityIdentifier) {
        try {
            return restClient.get()
                .uri("/api/v1/internal/qualifications/verification-snapshots/by-security-identifier/{id}",
                    securityIdentifier)
                .headers(headers -> addAuthorization(headers, serviceToken))
                .retrieve()
                .body(QualificationVerificationSnapshotDto.class);
        } catch (HttpStatusCodeException ex) {
            if (HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
                throw new UpstreamNotFoundException("No authoritative qualification record found");
            }
            throw new UpstreamServiceException("Qualification service lookup failed", ex);
        } catch (RuntimeException ex) {
            throw new UpstreamServiceException("Qualification service is unavailable", ex);
        }
    }

    private void addAuthorization(HttpHeaders headers, String token) {
        if (StringUtils.hasText(token)) {
            headers.setBearerAuth(token);
        }
    }
}
