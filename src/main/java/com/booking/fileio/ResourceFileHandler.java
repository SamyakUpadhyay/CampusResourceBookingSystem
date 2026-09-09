package com.booking.fileio;

import com.booking.model.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles serialization and deserialization of resource data to and from {@code data/resources.dat}.
 */
public class ResourceFileHandler implements FileHandler<Resource> {

    private static final String DEFAULT_FILE_PATH = "data/resources.dat";

    private final String filePath;

    /**
     * Constructs a resource file handler using the default file path.
     */
    public ResourceFileHandler() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a resource file handler with a custom file path.
     *
     * @param filePath path to the resource data file
     */
    public ResourceFileHandler(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all resources from the persistent data file.
     * Returns an empty list if the file does not exist or cannot be read.
     *
     * @return list of deserialized {@link Resource} objects
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Resource> load() {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            Object data = inputStream.readObject();
            if (data instanceof List<?> loadedList) {
                return new ArrayList<>((List<Resource>) loadedList);
            }
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }

    /**
     * Saves all resources to the persistent data file.
     * Creates the {@code data/} directory if it does not exist.
     *
     * @param resources list of resources to persist
     * @throws IOException if an I/O error occurs while writing
     */
    @Override
    public void save(List<Resource> resources) throws IOException {
        File file = new File(filePath);
        ensureParentDirectoryExists(file);

        List<Resource> dataToWrite = resources == null ? new ArrayList<>() : new ArrayList<>(resources);

        Path target = file.toPath();
        Path tempFile = target.resolveSibling(file.getName() + ".tmp");

        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(tempFile))) {
            outputStream.writeObject(dataToWrite);
        }

        try {
            Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException e) {
            Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Returns the configured file path for resource data.
     *
     * @return the file path string
     */
    @Override
    public String getFilePath() {
        return filePath;
    }

    /**
     * Checks whether the resource data file exists on disk.
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
