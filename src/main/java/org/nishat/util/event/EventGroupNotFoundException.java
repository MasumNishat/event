package org.nishat.util.event;

/**
 * Exception thrown when an event group is not found.
 */
public class EventGroupNotFoundException extends EventException {

    public EventGroupNotFoundException(String groupName) {
        super("Event group not found: " + groupName);
    }
}
