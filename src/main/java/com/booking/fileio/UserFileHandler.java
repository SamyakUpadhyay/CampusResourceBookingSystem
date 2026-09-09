package com.booking.fileio;

import com.booking.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles serialization and deserialization of user data to and from {@code data/users.dat}.
 */
public class UserFileHandler implements FileHandler<User> {

    private static final String DEFAULT_FILE_PATH = "data/users.dat";

    private final String filePath;

    /**
     * Constructs a user file handler using the default file path.
     */
    public UserFileHandler() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a user file handler with a custom file path.
     *
     * @param filePath path to the user data file
     */
    public UserFileHandler(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all users from the persistent data file.
     * Returns an empty list if the file does not exist or cannot be read.
     *
     * @return list of deserialized {@link User} objects
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<User> load() {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            Object data = inputStream.readObject();
            if (data instanceof List<?> loadedList) {
                return new ArrayList<>((List<User>) loadedList);
            }
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }

    /**
     * Saves all users to the persistent data file.
     * Creates the {@code data/} directory if it does not exist.
     *
     * @param users list of users to persist
     * @throws IOException if an I/O error occurs while writing
     */
    @Override
    public void save(List<User> users) throws IOException {
        File file = new File(filePath);
        ensureParentDirectoryExists(file);

        List<User> dataToWrite = users == null ? new ArrayList<>() : new ArrayList<>(users);

        Path target = file.toPath();
        Path tempFile = target.resolveSibling(file.getName() + ".tmp");

        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(tempFile))) {
            outputStream.writeObject(dataToWrite);
        }

        // Atomically replace the real file only once the temp file has been
        // fully and successfully written, so a crash mid-write never leaves
        // users.dat truncated or corrupted.
        try {
            Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException e) {
            // Some filesystems (notably certain network/FAT mounts) don't support
            // atomic moves; fall back to a plain (still whole-file) replace.
            Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Returns the configured file path for user data.
     *
     * @return the file path string
     */
    @Override
    public String getFilePath() {
        return filePath;
    }

    /**
     * Checks whether the user data file exists on disk.
     *
     * @return {@code true} if the file exists
     */
    @Override
    public boolean fileExists() {
        return new File(filePath).exists();
    }

    private void ensureParentDirectoryExists(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create directory: " + parent.getAbsolutePath());
        }
    }
}
