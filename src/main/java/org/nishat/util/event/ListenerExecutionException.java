package org.nishat.util.event;

/**
 * Exception thrown when a listener fails during execution.
 * This wraps the original exception thrown by the listener.
 */
public class ListenerExecutionException extends EventException {

    private final String listenerId;
    private final String eventName;

    public ListenerExecutionException(String listenerId, String eventName, Throwable cause) {
        super("Listener execution failed: " + listenerId + " in event: " + eventName, cause);
        this.listenerId = listenerId;
        this.eventName = eventName;
    }

    public String getListenerId() {
        return listenerId;
    }

    public String getEventName() {
        return eventName;
    }
}
