package org.nishat.util.event;

/**
 * Exception thrown when a listener fails during execution.
 * This wraps the original exception thrown by the listener.
 */
public class ListenerExecutionException extends EventException {

    /** The ID of the listener that failed. */
    private final String listenerId;

    /** The name of the event being executed when the listener failed. */
    private final String eventName;

    /**
     * Constructs a new listener execution exception.
     *
     * @param listenerId the ID of the listener that failed
     * @param eventName the name of the event being executed
     * @param cause the exception thrown by the listener
     */
    public ListenerExecutionException(String listenerId, String eventName, Throwable cause) {
        super("Listener execution failed: " + listenerId + " in event: " + eventName, cause);
        this.listenerId = listenerId;
        this.eventName = eventName;
    }

    /**
     * Gets the ID of the listener that failed.
     *
     * @return the listener ID
     */
    public String getListenerId() {
        return listenerId;
    }

    /**
     * Gets the name of the event that was being executed when the listener failed.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }
}
