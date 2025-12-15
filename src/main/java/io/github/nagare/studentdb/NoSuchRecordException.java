package io.github.nagare.studentdb;

/**
 * Exception thrown when a requested record is not found in the database.
 */
public class NoSuchRecordException extends Exception {

    public NoSuchRecordException() {
        super();
    }

    public NoSuchRecordException(String message) {
        super(message);
    }

    public NoSuchRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
