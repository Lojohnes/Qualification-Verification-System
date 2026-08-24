package com.aqvp.platform.qualification.service;

/**
 * Stores and retrieves generated document content.
 */
public interface DocumentStorageService {

    String store(String fileName, byte[] content);

    byte[] read(String storageKey);
}
