package com.booking.fileio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manages append-only activity logging to a {@code .txt} log file.
 * <p>
 * Implemented as a Singleton so that only one writer is ever responsible for
 * appending to the shared audit log, avoiding interleaved/corrupted writes.
 */
public class LogManager {

    private static final String DEFAULT_LOG_PATH = "data/audit_log.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final LogManager INSTANCE = new LogManager();

    private final String logFilePath;

    /**
     * Constructs a log manager using the default log file path.
     */
    public LogManager() {
        this(DEFAULT_LOG_PATH);
    }

    /**
     * Constructs a log manager with a custom log file path.
     *
     * @param logFilePath path to the activity log file
     */
    public LogManager(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    /**
     * Returns the shared singleton instance writing to the default audit log file.
     *
     * @return the shared {@link LogManager} instance
     */
    public static LogManager getInstance() {
        return INSTANCE;
    }

    /**
     * Writes an informational log entry to the log file.
     *
     * @param message the message to record
     */
    public void logInfo(String message) {
        log(LocalDateTime.now(), "INFO", message);
    }

    /**
     * Writes a warning log entry to the log file.
     *
     * @param message the warning message to record
     */
    public void logWarning(String message) {
        log(LocalDateTime.now(), "WARN", message);
    }

    /**
     * Writes an error log entry to the log file.
     *
     * @param message the error message to record
     */
    public void logError(String message) {
        log(LocalDateTime.now(), "ERROR", message);
    }

    /**
     * Writes a log entry with an explicit timestamp.
     * Uses a try/catch/finally block so a failed write never crashes the caller;
     * failures are silently swallowed since logging is a best-effort side channel.
     *
     * @param timestamp date and time of the event
     * @param level     severity level label
     * @param message   the message to record
     */
    public synchronized void log(LocalDateTime timestamp, String level, String message) {
        File file = new File(logFilePath);
        PrintWriter writer = null;
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Unable to create directory: " + parent.getAbsolutePath());
            }
            writer = new PrintWriter(new FileWriter(file, true));
            String line = String.format("[%s] [%s] %s",
                    timestamp.format(TIMESTAMP_FORMAT), level, message);
            writer.println(line);
        } catch (IOException e) {
            // Logging is best-effort; the application must keep running even if the
            // audit log cannot currently be written to disk.
            System.err.println("Failed to write to audit log: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Returns the configured log file path.
     *
     * @return the log file path string
     */
    public String getLogFilePath() {
        return logFilePath;
    }

    /**
     * Checks whether the log file exists on disk.
     *
     * @return {@code true} if the log file exists
     */
    public boolean logFileExists() {
        return new File(logFilePath).exists();
    }
}
