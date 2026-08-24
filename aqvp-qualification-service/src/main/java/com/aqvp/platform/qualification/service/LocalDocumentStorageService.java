package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.config.DocumentProperties;
import com.aqvp.platform.qualification.exception.BusinessException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Filesystem-backed document storage for local/dev deployments.
 */
@Service
@RequiredArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed configuration bean injection")
public class LocalDocumentStorageService implements DocumentStorageService {

    private final DocumentProperties documentProperties;

    @Override
    public String store(String fileName, byte[] content) {
        final LocalDate today = LocalDate.now();
        final String storageKey = today.getYear() + "/" + pad(today.getMonthValue()) + "/"
                + UUID.randomUUID() + "-" + sanitize(fileName);
        final Path target = resolve(storageKey);
        try {
            final Path parent = target.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(target, content);
            return storageKey;
        } catch (IOException ex) {
            throw new BusinessException("Unable to store generated document", ex);
        }
    }

    @Override
    public byte[] read(String storageKey) {
        try {
            return Files.readAllBytes(resolve(storageKey));
        } catch (IOException ex) {
            throw new BusinessException("Unable to read generated document", ex);
        }
    }

    private Path resolve(String storageKey) {
        final Path root = Path.of(documentProperties.getStorageRoot()).toAbsolutePath().normalize();
        final Path resolved = root.resolve(storageKey).normalize();
        if (!resolved.startsWith(root)) {
            throw new BusinessException("Document storage key resolves outside configured storage root");
        }
        return resolved;
    }

    private String sanitize(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "-");
    }

    private String pad(int value) {
        return value < 10 ? "0" + value : Integer.toString(value);
    }
}
