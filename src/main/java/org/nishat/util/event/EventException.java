package org.nishat.util.event;

/**
 * Base exception for all event-related errors.
 * This is the parent class for all custom exceptions thrown by the event library.
 */
public class EventException extends RuntimeException {

    /**
     * Constructs a new event exception with the specified detail message.
     *
     * @param message the detail message explaining the exception
     */
    public EventException(String message) {
        super(message);
    }

    /**
     * Constructs a new event exception with the specified detail message and cause.
     *
     * @param message the detail message explaining the exception
     * @param cause the cause of this exception (which is saved for later retrieval)
     */
    public EventException(String message, Throwable cause) {
        super(message, cause);
    }
}
