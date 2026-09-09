package com.booking.fileio;

import java.io.IOException;
import java.util.List;

/**
 * Common contract implemented by every persistence handler in
 * {@code com.booking.fileio}. Centralising the load/save shape here means a
 * new persisted entity only needs its own {@code FileHandler<T>}
 * implementation, and calling code (services, tests) can depend on this
 * abstraction rather than a concrete handler class.
 *
 * @param <T> the type of object this handler persists
 */
public interface FileHandler<T> {

    /**
     * Loads all persisted instances of {@code T} from storage.
     * Implementations should return an empty list (never {@code null})
     * if the backing file does not exist or cannot be read.
     *
     * @return the loaded list of records
     */
    List<T> load();

    /**
     * Persists the given list of records to storage, replacing whatever was
     * previously stored.
     *
     * @param items the records to persist
     * @throws IOException if an I/O error occurs while writing
     */
    void save(List<T> items) throws IOException;

    /**
     * Returns the configured file path backing this handler.
     *
     * @return the file path string
     */
    String getFilePath();

    /**
     * Checks whether the backing file currently exists on disk.
     *
     * @return {@code true} if the file exists
     */
    boolean fileExists();
}
