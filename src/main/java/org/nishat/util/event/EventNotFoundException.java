package org.nishat.util.event;

/**
 * Exception thrown when an event is not found.
 * This exception is thrown when attempting to access or manipulate an event that doesn't exist.
 */
public class EventNotFoundException extends EventException {

    /**
     * Constructs a new event not found exception for the specified event name.
     *
     * @param eventName the name of the event that was not found
     */
    public EventNotFoundException(String eventName) {
        super("Event not found: " + eventName);
    }

    /**
     * Constructs a new event not found exception for the specified event name and group.
     *
     * @param eventName the name of the event that was not found
     * @param group the name of the group where the event was expected
     */
    public EventNotFoundException(String eventName, String group) {
        super("Event not found: " + eventName + " in group: " + group);
    }
}
