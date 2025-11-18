package org.nishat.util.event;

/**
 * Exception thrown when an event is not found.
 */
public class EventNotFoundException extends EventException {

    public EventNotFoundException(String eventName) {
        super("Event not found: " + eventName);
    }

    public EventNotFoundException(String eventName, String group) {
        super("Event not found: " + eventName + " in group: " + group);
    }
}
